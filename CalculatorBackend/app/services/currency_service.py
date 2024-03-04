import os
import requests
import datetime
from dotenv import load_dotenv
from app.utils.errors import NotFoundError

load_dotenv()

class CurrencyService:
    def __init__(self, redis_client):
        self.redis_client = redis_client
        self.app_id = os.getenv("APP_ID")

    def validate_currency_code(self, code):
        # valid ISO 4217 codes
        valid_codes = ["USD", "EUR", "GBP", "AED", "COL", "AUD"]

        if code not in valid_codes:
            raise NotFoundError(f"Invalid currency code: {code}")

    def get_conversion_rate(self, from_currency, to_currency):
        self.validate_currency_code(from_currency)
        self.validate_currency_code(to_currency)

        cache_key = f"{from_currency}_{to_currency}"
        cached = self.redis_client.get_value(cache_key).decode('utf-8') if self.redis_client.get_value(cache_key) else None
        # cached = self.redis_client.get_value(cache_key)

        if cached:
            rate, timestamp = cached.split('|')
            return {'rate': float(rate), 'timestamp': timestamp}

        # Fetch from Open Exchange Rates if not cached or cache is outdated
        url = f'https://openexchangerates.org/api/convert/1/{from_currency}/{to_currency}?app_id={self.app_id}'
        response = requests.get(url).json()
        rate = response['meta']['rate']

        # Generate timestamp at the moment of caching
        timestamp = datetime.datetime.utcnow().isoformat() + "Z"

        # Cache the new rate with timestamp
        self.redis_client.set_value(cache_key, f"{rate}|{timestamp}", 3600)  # Cache for 1 hour
        return {'rate': rate, 'timestamp': timestamp}