# engine ai disinilah semuanya menjadi
import cv2
import os
import numpy as np
import time
import pandas as pd
from pathlib import Path
import pymongo
import datetime
import pika
import json

# -------------------------------
# Setup mongo configuration
myclient = pymongo.MongoClient("mongodb://localhost:27017/")
mydb = myclient["skymind"]
mycol_employee = mydb["employee"]
mycol_attendance = mydb["attendance"]


# -------------------------------

def message(emp_num):
    date = str(datetime.datetime.now().date())
    dt_obj = datetime.datetime.strptime(date, '%Y-%m-%d')
    date = str(int(dt_obj.timestamp() * 1000))
    print(date)

    time = str(int(datetime.datetime.now().timestamp() * 1000))
    print(time)

    x = {
        "emp_num": emp_num,
        "date": date,
        "time_in": time,
        "time_out": time
    }

    # convert into JSON:
    y = json.dumps(x)

    print(y)

    return y


def rabbit_sender(messages):
    # establish the connection
    connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
    attendance = connection.channel()

    # the queue that we want to use, where we need to declare it's first
    attendance.queue_declare(queue='attendance')

    # There are 3 thing needs to be configured in order to send the message
    # 1. Exchange
    # 2. Routing key is the queue name that has been declared
    # 3. Body

    attendance.basic_publish(exchange='',
                             routing_key='attendance',
                             body=messages)

    print(" [x] Message has been send")

    # close the connection
    connection.close()

    # rabbit_receiver()
    # return {rabbit-receiver : rabbit should receive emp_num from java application}


def capture_data(haar_cascade_path, img_dataset, database_path):
    # take image from the camera
    cam = cv2.VideoCapture(0)

    # to know the fps of the camera
    # print(cam.get(cv2.CAP_PROP_FPS))

    # minimum size to be recognized as a face
    minW = int(cam.get(3) / 3)  # width of the picture
    minH = int(cam.get(4) / 3)  # height of the picture

    # maximum sample to be taken
    max_sample = 30

    # initialize haar-cascade
    face_detection = cv2.CascadeClassifier(haar_cascade_path)

    # Enter employee number
    emp_num = int(input('\n Enter Employee Number : '))

    # try to query employee number requireed
    myquery = {"emp_num": emp_num}
    mydoc = mycol_employee.find(myquery)

    # initialize database = 0, later if query cannot found the employee
    # number it will return 0
    database = 0

    # check row by row of the employee number
    for row_status in mydoc:
        database = row_status.get("emp_num")

    # Initialize individual sampling face count
    count = 0

    # if the employee number are registred in database then
    # the image capturing process will begin
    if (emp_num == database):
        print("\n Please look at the camera and wait.....")

        # using while loop in order to extract the image frame one by one
        while (True):
            # Take image frame one by one
            ret, img = cam.read()

            # img = cv2.flip(img, -1) # flip video image vertically

            # change from color to grrayscale
            gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

            # detect face
            faces = face_detection.detectMultiScale(gray, 1.2, 5, minSize=(minW, minH))

            # using for loop to extract (x,y,w,h) from faces
            for (x, y, w, h) in faces:
                # to calculate the progress of image that has been captured
                count += 1
                counter = count / 100 * 100

                # put the rectangle on the detected faces
                cv2.rectangle(img, (x, y), (x + w, y + h), (255, 0, 0), 2)

                # cv2.putText(img, str(counter)+"%", (x+5,y+h-5), font, 1, (255,255,0), 2)
                cv2.putText(img, str("{:.2f} %".format(counter)), (x + 5, y + h - 5), cv2.FONT_HERSHEY_SIMPLEX, 1,
                            (255, 255, 0), 2)

                # Save the captured image into the datasets folder
                cv2.imwrite(img_dataset + str(emp_num) + '.' + str(count) + ".jpg", gray[y:y + h, x:x + w])

            # To make the window is sizeable
            cv2.namedWindow('image', cv2.WINDOW_NORMAL)

            # To make window open full screen without titlebar
            cv2.setWindowProperty("image", cv2.WND_PROP_FULLSCREEN, cv2.WINDOW_FULLSCREEN)

            # show the image and rectangle of detected faces
            cv2.imshow('image', img)

            k = cv2.waitKey(100) & 0xff  # Press 'ESC' for exiting video
            if k == 27:
                cam.release()
                cv2.destroyAllWindows()
                break
            elif count >= max_sample:  # Take 30 face sample and stop video
                cam.release()
                cv2.destroyAllWindows()
                break

    else:
        print("Employee number does not exist")


def train_data(haar_cascade_path, img_dataset, trained_model):
    # font to be used at rectangle
    font = cv2.FONT_HERSHEY_SIMPLEX

    # machine path
    home = str(Path.home())

    face_detection = cv2.CascadeClassifier(haar_cascade_path)

    # 3. Path for LBPH pretrained model
    face_recognition = cv2.face.LBPHFaceRecognizer_create()

    print("Please wait for model to train")

    img_datasets = [os.path.join(img_dataset, f) for f in os.listdir(img_dataset)]

    # empty array for face samples
    face_sample = []

    # empy array for ids
    emp_num = []

    count_sample = 0
    for image in img_datasets:

        # Read data in grayscale mode
        gray = cv2.imread(image, 0)

        # get the id only from the path name file
        id = int(os.path.split(image)[-1].split(".")[0])
        # print(id)
        # faces = face_detection.detectMultiScale(gray)
        faces = face_detection.detectMultiScale(gray, 1.2, 5, minSize=(213, 160))

        for (x, y, w, h) in faces:
            # LBPH
            face_sample.append(gray[y:y + h, x:x + w])
            emp_num.append(id)

        count_sample += 1
        progress = count_sample / len(img_datasets) * 100
        print("progress to train {:.2f} %".format(progress))
        # -------------------------------------
    face_recognition.train(face_sample, np.array(emp_num))
    # Save the model into trainer/trainer.yml
    face_recognition.save(trained_model)  # recognizer.save() worked on Mac, but not on Pi #asal recognizer.write
    # Print the numer of faces trained and end program
    print("\n [INFO] {0} faces trained. Exiting Program".format(len(np.unique(emp_num))))


def recognize(haar_cascade_path, trained_model, datebase_path):
    # take image from the camera
    cam = cv2.VideoCapture(0)

    print(cam.get(cv2.CAP_PROP_FPS))

    # font to be used at rectangle
    font = cv2.FONT_HERSHEY_SIMPLEX

    # MINIMUM SIZE TO BE RECOGNIZED AS A FACE
    minW = int(cam.get(3) / 3)
    minH = int(cam.get(4) / 3)

    home = str(Path.home())
    face_detection = cv2.CascadeClassifier(haar_cascade_path)

    # 3. Path for LBPH pretrained model
    face_recognition = cv2.face.LBPHFaceRecognizer_create()

    min_acc = 20

    # READ WEIGHT OF LPBH TRAINED MODEL
    face_recognition.read(trained_model)

    # open databese file
    database = pd.read_csv(database_path)

    fpsanalysis = []

    while True:
        start_time = time.time()  # start time of the loop
        ret, img = cam.read()
        #     img = cv2.flip(img, -1) # Flip vertically
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

        faces = face_detection.detectMultiScale(gray, 1.2, 5, minSize=(minW, minH))
        # emp_name = "aba"

        # this is timer for cases where it does not detect the faces
        result = 1.0 / (time.time() - start_time)
        strs = "FPS : {:.0f}".format(result)

        for (x, y, w, h) in faces:
            cv2.rectangle(img, (x, y), (x + w, y + h), (0, 255, 0), 2)

            # LPBH
            emp_num, confidence = face_recognition.predict(gray[y:y + h, x:x + w])

            # find the row of employee number (eg. = 7)
            emp_name = database.loc[database['Employee Number'] == emp_num]

            # after found employee number show the name of the employee by taking
            # the -name- column
            emp_name = emp_name['Employee Name'].values[0]

            acc = round(100 - confidence)

            if (acc > min_acc):
                # bundle all the message and change it to the json format
                messages = message(emp_num)

                # rabbitmq send the messages
                rabbit_sender(messages)

                # rabbit_sender(emp_num) # rabbit should return emp_num

                emp_name
            else:
                emp_name = "unknown"

            # cv2.putText(img, 'Izham Pass : Door Opens', (10,40), font, 1, (255,255,255), 2)
            cv2.putText(img, str(emp_name), (x, y - 5), font, 1, (255, 255, 255), 2)
            cv2.putText(img, "{} %".format(str(acc)), (x + 5, y + h - 5), font, 1, (255, 255, 0), 1)

            result = 1.0 / (time.time() - start_time)
            strs = "FPS : {:.0f}".format(result)  # FPS = 1 / time to process loop
            fpsanalysis.append(result)

        # cosmetic : put text on the picture
        cv2.putText(img, strs, (0, 50), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 255, 0), 3)
        # ----------------------------------------

        cv2.namedWindow('image', cv2.WINDOW_NORMAL)
        # cv2.setWindowProperty("image", cv2.WND_PROP_FULLSCREEN, cv2.WINDOW_FULLSCREEN);
        cv2.imshow('image', img)

        k = cv2.waitKey(10) & 0xff  # Press 'ESC' for exiting video
        if k == 27:
            cam.release()
            cv2.destroyAllWindows()
            break


# show popup to choose whether to capture/train new data or to recognize
while (True):
    # to know machine path
    # home = str(Path.home())
    home = "/data/Gudang-Ilmu/Ubuntu-Home/Documents/Github/original/ai-engine"

    haar_cascade_path = home + '/haarcascade_frontalface_default.xml'
    trained_model = home + '/trainer.yml'
    img_dataset = home + '/dataset/'
    database_path = home + "/face_database.csv"

    try:
        choice = int(input("\n1. Capture data"
                           "\n2. Train data"
                           "\n3. Recognition face"
                           "\n\nEnter your choice : "))

        if (choice == 1):
            # capturing the dataset
            capture_data(haar_cascade_path, img_dataset, database_path)

        elif (choice == 2):
            # train the data
            train_data(haar_cascade_path, img_dataset, trained_model)

        elif (choice == 3):
            # recognize/deploy the system
            recognize(haar_cascade_path, trained_model, database_path)

        # elif(choice==4):
        #     test_accuracy()

        else:
            exit()

    except Exception as e:
        print(e)
