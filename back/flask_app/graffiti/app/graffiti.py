from flask import Flask, request
from flask_socketio import SocketIO

app = Flask(__name__)
socketio = SocketIO(app)

@app.route('/')
@app.route('/index')
def index():
    return "Hello, welcome to the page."

@app.route('/make_post', methods=['POST'])
def make_post():
    if request.headers['Content-Type'] == 'application/json':
        json_dict = request.json
        data_tuple = (json_dict["latitude"], json_dict["longitude"], json_dict["text"])
        print(data_tuple)
        return "Nice bro"
    else:
        return {"invalid" : "please only post JSON, and not cringe"}, 400

@app.route('/connect')
def connect():
    return """<script src="//cdnjs.cloudflare.com/ajax/libs/socket.io/2.2.0/socket.io.js" integrity="sha256-yr4fRk/GU1ehYJPAs8P4JlTgu0Hdsp4ZKrx8bDEDC3I=" crossorigin="anonymous"></script>
<script type="text/javascript" charset="utf-8">
    var socket = io();
    socket.on('connect', function() {
        socket.emit('location', "349487, 345069843");
    });

    socket.on('show_posts', function(data) {
        console.log(data)
    });
</script>"""

@socketio.on('location')
def handle_location(local):
    posts = retrieve_posts(local)
    found_posts = 1 if (posts != None) else 0

    response_dict = {
                        "found_posts" : found_posts,
                        "posts" : str(posts)
    }

    socketio.emit('show_posts', str(response_dict));


def retrieve_posts(local):
    post_list = []
    return post_list

@socketio.on('connect')
def handle_connect():
    print("CLIENT CONNECTED")

if __name__ == '__main__':
    socketio.run(app)
