from flask import Flask, request
app = Flask(__name__)

@app.route('/')
def hello_world():
    auth = request.authorization
    return 'Hello ' + auth.username

if __name__ == '__main__':
    app.run()