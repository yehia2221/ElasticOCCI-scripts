#!/usr/bin/env python
from flask import Flask
from flask import jsonify
from redis import Redis
import os
import netifaces as ni # net ifaces package to get ip

app = Flask(__name__)
redis = Redis(host='ip', port=6379)

@app.route('/')
def hello():
    redis.incr('hits')
    return 'Hello World! I have been seen %s times.' % redis.get('hits')

@app.route('/compute/<int:number>', methods=['GET'])
    #ni.ifaddresses('eth0')
def fib(number):
    ip = ni.ifaddresses('ens160')[ni.AF_INET][0]['addr']
    a = 0
    b = 1
    for i in xrange(0, number - 1):
        temp = a
        a = b
        b = temp + b
        redis.rpush('value', str(a))
    return jsonify({'ip ': ip}, {'result': a})

if __name__ == "__main__":
#    app.run(host="0.0.0.0", port=5000, debug=True)
     app.run(host="0.0.0.0", port=80, processes=6, debug=True)
