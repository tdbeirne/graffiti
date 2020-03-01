from flask import Flask, request, g, json
from flask_socketio import SocketIO
from math import radians, cos, sin, asin, sqrt
import sqlite3
import sys
import time
import random
from math import acos

DATABASE = '/opt/data/graffiti.db'
TABLE_NAME = 'messages'
CENTER_X, CENTER_Y = 40.1004427035469, -88.22811081366866 # center of uiuc

app = Flask(__name__)
socketio = SocketIO(app)

@app.route('/')
@app.route('/index')
def index():
    return "Hello, welcome to the page."

@app.route('/make_post', methods=['POST'])
def make_post():
    post_data = request.get_json()
    if post_data is None:
        return {"invalid" : "please only post JSON, and not cringe"}, 400

    data = (post_data["latitude"], post_data["longitude"], post_data["text"], int(time.time()))
    create_post(data)
    return "Nice bro"

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
    found_posts = posts != None

    response_dict = {
                        "found_posts" : found_posts,
                        "posts" : str(posts)
    }

    socketio.emit('show_posts', str(response_dict))

@socketio.on('connect')
def handle_connect():
    print("CLIENT CONNECTED")

@app.teardown_appcontext
def close_connection(exception):
    db = getattr(g, '_database', None)
    if db is not None:
        db.close()

# DATABASE FUNCTIONS

def get_db():
    db = getattr(g, '_database', None)
    if db is None:
        try:
            db = g._database = sqlite3.connect(DATABASE)
        except sqlite3.Error as e:
            print(e)
    return db

def fetch_query(query: str):
    cur = get_db().cursor()
    cur.execute(query)
    rows = cur.fetchall()
    return rows

@app.route('/gen')
def gen_random():
    conn = get_db()
    cur = conn.cursor()
    for _ in range(100):
        x, y = random.uniform(CENTER_X - .2, CENTER_X + .2), random.uniform(CENTER_Y - .2, CENTER_Y + .2)
        data = (x, y, "DIE", time.time())
        query = "INSERT INTO {} (lat, lon, txt, time) VALUES {}".format(TABLE_NAME, str(data))
        cur.execute(query)
    conn.commit()
    return "Ok idiot"

def create_post(data_tuple):
    conn = get_db()
    cur = conn.cursor()
    query = "INSERT INTO {} (lat, lon, txt, time) VALUES{}".format(TABLE_NAME, str(data_tuple))
    cur.execute(query)
    conn.commit()
    print("WROTE TO DB")

@app.route('/rad/<num>')
def get_random_dudes(num):
    messages = find_messages_in_radius(CENTER_X, CENTER_Y, int(num))
    # print(messages)
    total = retrieve_posts(None)
    # print(total)
    return "Found {} messages in radius {}m out of {} total".format(len(messages), num, len(total))

def find_messages_in_radius(lat: float, lon: float, m: int):
    query = "SELECT * FROM {} WHERE lat BETWEEN {} AND {} AND lon BETWEEN {} AND {};".format(TABLE_NAME, lat-.2, lat+.2, lon-.2, lon+.2)
    candidate_messages = fetch_query(query)
    messages_in_radius = []
    for msg in candidate_messages:
        (_, clat, clon, _, _) = msg
        if lat_long_converter(clat, clon) < m:
            messages_in_radius.append(msg)
    return messages_in_radius


def retrieve_posts(local):
    return fetch_query("SELECT * FROM {}".format(TABLE_NAME))

def lat_long_converter(lat, lon):
    return acos(sin(1.3963) * sin(lat) + cos(1.3963) * cos(lat) * cos(lon - (-0.6981))) * 6371

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