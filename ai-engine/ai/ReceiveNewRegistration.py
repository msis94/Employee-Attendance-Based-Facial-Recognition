import pika, sys, os
import json
import pandas as pd
from pathlib import Path

def main():
    connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
    channel = connection.channel()
    channel.queue_declare(queue='employee')

    def callback(ch, method, properties, body):
        try:

            y = json.loads(body)
            # y = json.loads(y["message"])
            print(y["emp_name"])
            cars = {'Employee Name': [y['emp_name']],'Employee Number': [y['emp_num']]}
            df = pd.DataFrame(cars, columns= ['Employee Name', 'Employee Number'])
            home = str(Path.home())
            df.to_csv(home+'/Desktop/original/ai-engine/face_database.csv', mode='a', index = False, header=False)

        except:
            print("go on")

#save to csv file

    channel.basic_consume(queue='employee', on_message_callback=callback, auto_ack=True)
    print(' [*] Waiting for messages. To exit press CTRL+C')
    channel.start_consuming()

if __name__ == '__main__':
    try:
        main()
    except KeyboardInterrupt:
        print('Interrupted')
        try:
            sys.exit(0)
        except SystemExit:
            os._exit(0)