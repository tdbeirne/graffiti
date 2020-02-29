from flask import Flask
from flask_socketio import SocketIO

app = Flask(__name__)
socketio = SocketIO(app)

@app.route('/')
@app.route('/index')
def index():
    return "Hello, welcome to the page."

# @app.route('/connect')
# def connect():
#     return """<script src="//cdnjs.cloudflare.com/ajax/libs/socket.io/2.2.0/socket.io.js" integrity="sha256-yr4fRk/GU1ehYJPAs8P4JlTgu0Hdsp4ZKrx8bDEDC3I=" crossorigin="anonymous"></script>
# <script type="text/javascript" charset="utf-8">
#     var socket = io();
#     socket.on('connect', function() {
#         socket.emit('test', "THIS IS A TEST");
#     });
# </script>"""

@socketio.on('location')
def handle_location(local):
    print('received location: ' + str(local))

@socketio.on('connect')
def handle_connect():
    print("CLIENT CONNECTED")

if __name__ == '__main__':
    socketio.run(app)
