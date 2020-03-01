from flask import Flask, request, g, json
from flask_socketio import SocketIO
from math import radians, cos, sin, asin, sqrt
import sqlite3
import sys
import time
import random
import json
from math import acos

DATABASE = '/opt/data/graffiti.db'
TABLE_NAME = 'messages'
CENTER_X, CENTER_Y = 40.1004427035469, -88.22811081366866 # center of uiuc

app = Flask(__name__)
socketio = SocketIO(app)

@app.route('/')
@app.route('/index')
def index():
    return "Hello, welcome to Graffiti!"

@app.route('/delete-all', methods=['GET'])
def delete_all():
    conn = get_db()
    cur = conn.cursor()

    #delete all rows from table
    query = "DELETE FROM {}".format(TABLE_NAME)
    cur.execute(query)
    conn.commit()

    return "DELETED ALL MESSAGES"

@app.route('/make_post', methods=['POST'])
def make_post():
    post_data = request.get_json()
    if post_data is None:
        return json.dumps({"invalid" : "Please only post JSON."}), 400

    data = (post_data.get("latitude"), post_data.get("longitude"), post_data.get("text"), int(time.time()))

    #check that no values are missing
    for item in data:
        if not item:
            return json.dumps({"invalid" : "Missing data fields. Please check your submission."}), 400

    create_post(data)
    return json.dumps({"posted": "true"})

def create_post(data_tuple):
    conn = get_db()
    cur = conn.cursor()

    #write to database
    query = "INSERT INTO {} (lat, lon, txt, time) VALUES{}".format(TABLE_NAME, str(data_tuple))
    cur.execute(query)
    conn.commit()

#URL for testing sockets
@socketio.on('connect')
def connect(sid, environ):
    print("connected", file=sys.stderr)

#receives location from user, outputs nearby posts
@socketio.on('location')
def handle_location(local):
    posts = find_messages_in_radius(local["latitude"], local["longitude"], 0.01)
    found_posts = (posts != None)

    response_dict = {
                        "found_posts" : found_posts,
                        "posts" : str(posts).lower()
    }

    socketio.emit('show_posts', str(response_dict))

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
    conn = get_db()
    conn.row_factory = sqlite3.Row
    cur = conn.cursor()
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

def find_messages_in_radius(lat: float, lon: float, m: float):
    query = "SELECT * FROM {} WHERE lat BETWEEN {} AND {} AND lon BETWEEN {} AND {};".format(TABLE_NAME, lat - m, lat + m, lon - m, lon + m)
    messages_in_radius = fetch_query(query)

    #return message text only
    return [msg["txt"] for msg in messages_in_radius]

def retrieve__all_posts(local):
    return fetch_query("SELECT * FROM {}".format(TABLE_NAME))

def lat_long_converter(lat, lon):
    return acos(sin(1.3963) * sin(lat) + cos(1.3963) * cos(lat) * cos(lon - (-0.6981))) * 6371

if __name__ == '__main__':
    socketio.run(app, host='0.0.0.0')
