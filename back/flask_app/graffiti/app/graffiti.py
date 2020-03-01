from flask import Flask, request, g
from flask_socketio import SocketIO
from math import radians, cos, sin, asin, sqrt
import sqlite3
import sys
import time

DATABASE = '/opt/data/graffiti.db'
TABLE_NAME = 'messages'

app = Flask(__name__)
socketio = SocketIO(app)

@app.route('/')
@app.route('/index')
def index():
    return "Hello, welcome to Graffiti!"

@app.route('/make_post', methods=['POST'])
def make_post():
    if request.headers['Content-Type'] == 'application/json':
        json_dict = request.json

        data_list = [json_dict.get("latitude"), json_dict.get("longitude"), json_dict.get("text"), int(time.time()]

        #check that no values are missing
        for item in data_list:
            if not item:
                return {"invalid" : "Missing data fields. Please check your submission."}, 400

        create_post(tuple(data_tuple))
        return {"submitted" : "true"}, 201
    else:
        return {"invalid" : "Please only post JSON"}, 400

def create_post(data_tuple):
    conn = get_db()
    cur = conn.cursor()

    #write to database
    query = "INSERT INTO {} (lat, lon, txt, time) VALUES{}".format(TABLE_NAME, str(data_tuple))
    cur.execute(query)
    conn.commit()

#URL for testing sockets
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

#receives location from user, outputs nearby posts
@socketio.on('location')
def handle_location(local):
    posts = retrieve_posts(local)
    found_posts = True if (posts != None) else False

    response_dict = {
                        "found_posts" : found_posts,
                        "posts" : str(posts).lower()
    }

    socketio.emit('show_posts', str(response_dict));

#retrieves posts that are near the user to the database
def retrieve_posts(local):
    conn = get_db()
    conn.row_factory = sqlite3.Row
    cur = conn.cursor()

    cur.execute("SELECT * FROM {}".format(TABLE_NAME))
    rows = cur.fetchall()

    #return message text only
    return [row["txt"] for row in rows]


#Methods for accessing database
def get_db():
    db = getattr(g, '_database', None)
    if db is None:
        db = g._database = sqlite3.connect(DATABASE)
    return db

@app.teardown_appcontext
def close_connection(exception):
    db = getattr(g, '_database', None)
    if db is not None:
        db.close()


if __name__ == '__main__':
    socketio.run(app, host='0.0.0.0')
