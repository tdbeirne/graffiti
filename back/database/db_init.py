import sqlite3
from sqlite3 import Error as SQLiteError

DATABASE = "./graffiti.db"
MAKE_TABLE = """CREATE TABLE IF NOT EXISTS messages (
    id INTEGER PRIMARY KEY,
    lat REAL NOT NULL,
    lon REAL NOT NULL,
    txt TEXT NOT NULL,
    time INTEGER NOT NULL
);"""
 
def create_connection(db: str) -> sqlite3.Connection:
    """ create a database connection to a SQLite database """
    c = None
    try:
        c = sqlite3.connect(db)
    except SQLiteError as e:
        print(e)
    return c

def sql_command(connection: sqlite3.Connection, sql: str):
    try:
        c: sqlite3.Cursor = connection.cursor()
        c.execute(sql)
    except SQLiteError as e:
        print(e)

def init():
    connection = create_connection(DATABASE)
    if connection is not None:
        sql_command(connection, MAKE_TABLE)



if __name__ == '__main__':
    init()