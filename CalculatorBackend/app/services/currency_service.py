import os
import requests
import datetime
from dotenv import load_dotenv
from requests.exceptions import HTTPError
from app.utils.errors import InvalidCurrencyCode, UnprocessableEntityError

load_dotenv()

class CurrencyService:
    def __init__(self, redis_client):
        self.redis_client = redis_client
        self.app_id = os.getenv("APP_ID")

    def validate_currency_code(self, code):
        # valid ISO 4217 codes
        valid_codes = ["USD", "EUR", "GBP", "AED", "COL", "AUD"]

        if code not in valid_codes:
            raise InvalidCurrencyCode(f"Invalid currency code: {code}")

    def get_conversion_rate(self, from_currency, to_currency):
        self.validate_currency_code(from_currency)
        self.validate_currency_code(to_currency)

        print(from_currency)
        print(to_currency)

        cache_key = f"{from_currency}_{to_currency}"
        cached = self.redis_client.get_value(cache_key).decode('utf-8') if self.redis_client.get_value(cache_key) else None

        if cached:
            print(f"{from_currency}/{to_currency} is already cached")

            rate, timestamp = cached.split('|')
            return {'rate': float(rate), 'timestamp': timestamp}

        try:
            url = f"https://openexchangerates.org/api/latest.json?app_id={self.app_id}&base={from_currency}&symbols={to_currency}&prettyprint=false&show_alternative=false"
            headers = {"accept": "application/json"}
            response = requests.get(url, headers=headers)

            actual_res = response.json()
            rate = actual_res["rates"][to_currency]

            print("JSON response")
            print(response.json())

            if rate is None :
                raise ValueError("Missing rate in the response")
            
            # Generate timestamp at the moment of caching
            timestamp = datetime.datetime.utcnow().isoformat() + "Z"

            # Cache the new rate with timestamp
            self.redis_client.set_value(cache_key, f"{rate}|{timestamp}", 3600)  # Cache for 1 hour
            return {'rate': rate, 'timestamp': timestamp}
        
        except HTTPError as http_err:
            # HTTP error occurred
            print(f"HTTP error occurred: {http_err}")  # Python 3.6+
            raise UnprocessableEntityError(message="Failed to fetch conversion rate due to an external API error.")
        except Exception as err:
            # Other errors
            print(f"An error occurred: {err}")
            raise UnprocessableEntityError(message="An unexpected error occurred while fetching conversion rate.")