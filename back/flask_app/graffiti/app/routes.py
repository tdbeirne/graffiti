@app.route('/')
@app.route('/index')
def index():
    print(dir(socketio))
    return app.config["SECRET_KEY"]
