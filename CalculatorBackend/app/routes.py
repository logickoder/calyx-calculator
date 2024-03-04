from . import app
from flask import request, jsonify
from .utils.rate_limiting import RateLimiter
from .utils.errors import InvalidCurrencyCode
from .services.currency_service import CurrencyService

rate_limiter = RateLimiter()

@app.before_request
def check_rate_limiting():
    ip = request.remote_addr

    if rate_limiter.is_rate_limited(ip):
        return jsonify({"error": "Rate limit exceeded"}), 429
    
# Define the home route
@app.route("/")
def home():
    return "Hello, World!"

# Define the test route
@app.route("/api/test")
def test():
    return "I'm just testing..."

@app.route('/api/conversion', methods=['GET'])
def conversion():
    from_currency = request.args.get('from')
    to_currency = request.args.get('to')
    
    try:
        currency_service = CurrencyService(app.redis_client)
        conversion_rate = currency_service.get_conversion_rate(from_currency.upper(), to_currency.upper())

        response = {
            "from": from_currency.upper(),
            "to": to_currency.upper(),
            "rate": conversion_rate['rate'],
            "timestamp": conversion_rate['timestamp']
        }

        return jsonify(response), 200
    
    except InvalidCurrencyCode as e:
        return jsonify({"error": str(e)}), 400