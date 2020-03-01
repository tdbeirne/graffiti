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
    return "Hello, welcome to the page."

@app.route('/make_post', methods=['POST'])
def make_post():
    if request.headers['Content-Type'] == 'application/json':
        json_dict = request.json
        data_tuple = (json_dict["latitude"], json_dict["longitude"], json_dict["text"], int(time.time()))
        create_post(data_tuple)
        return "Nice bro"
    else:
        return {"invalid" : "please only post JSON, and not cringe"}, 400

def create_post(data_tuple):
    conn = get_db()
    cur = conn.cursor()
    query = "INSERT INTO {} (lat, lon, txt, time) VALUES{}".format(TABLE_NAME, str(data_tuple))
    results = cur.execute(query)
    conn.commit()

    print("WROTE TO DB")


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
    cur = get_db().cursor()

    cur.execute("SELECT * FROM {}".format(TABLE_NAME))
    rows = cur.fetchall()

    return rows

@socketio.on('connect')
def handle_connect():
    print("CLIENT CONNECTED")

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


#function for getting distance between two points on earth in km (longitude, latitude)
def get_haversine_dist(lon1, lat1, lon2, lat2):
    # convert decimal degrees to radians
    lon1, lat1, lon2, lat2 = map(radians, [lon1, lat1, lon2, lat2])

    dlon = lon2 - lon1
    dlat = lat2 - lat1
    a = sin(dlat/2)**2 + cos(lat1) * cos(lat2) * sin(dlon/2)**2
    c = 2 * asin(sqrt(a))
    r = 6371 # Radius of earth in kilometers. Use 3956 for miles
    return c * r


if __name__ == '__main__':
    socketio.run(app, host='0.0.0.0')
