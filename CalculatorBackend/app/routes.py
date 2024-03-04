from . import app

# Define the home route
@app.route("/")
def home():
    return "Hello, World!"

# Define the test route
@app.route("/test")
def test():
    return "I'm just testing..."