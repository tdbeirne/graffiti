FROM python:3.7

RUN mkdir ./data

COPY ./graffiti /app

WORKDIR /app

RUN pip3 install -r requirements.txt

ENTRYPOINT ["python3"]

CMD ["./app/graffiti.py"]
