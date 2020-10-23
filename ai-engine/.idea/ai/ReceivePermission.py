import pika, sys, os
import json
import pandas as pd
from pathlib import Path

def main():
    connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
    channel = connection.channel()
    channel.queue_declare(queue='hello')

    def callback(ch, method, properties, body):
        y = json.loads(body)
        y = json.loads(y["message"])
        # print(y["emp_status"])
        if(y["emp_status"]=="active"):
            print("Door Opens for : ",y["emp_name"])
        else:
            print("Door Not Opens for : ",y["emp_name"])


    channel.basic_consume(queue='hello', on_message_callback=callback, auto_ack=True)
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