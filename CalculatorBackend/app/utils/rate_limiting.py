from time import time
from flask import request, g

class RateLimiter:
    def __init__(self):
        self.requests = {}
        # self.limit = 60
        self.limit = 2
        self.reset_time = 3600  # 1 hour

    def is_rate_limited(self, ip):
        current_time = time()
        requests_info = self.requests.get(ip, {'count': 0, 'time': current_time})

        if current_time - requests_info['time'] > self.reset_time:
            self.requests[ip] = {'count': 1, 'time': current_time}
            return False
        
        if requests_info['count'] >= self.limit:
            return True

        self.requests[ip]['count'] += 1
        return False