from flask import Flask

# Initialize the Flask application
app = Flask(__name__)

# Import the routes from routes.py
from . import routes