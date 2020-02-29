FROM python:3.7

COPY ./graffiti /app

WORKDIR /app

RUN pip3 install -r requirements.txt

ENTRYPOINT ["python3"]

CMD ["graffiti_back.py"]
