# s1p-19-demo
SpringOne platform 2019 conference - demo project

run with docker-compose

```
./run.sh

```
URL for Swagger

```
http://localhost:2021/swagger-ui.html

```


run the application as a spring-boot app 

```
mvn spring-boot:run
```

or 

```
run DemoApplication.java as Java application
```

for getting a Match info 

```
curl -X GET http://localhost:2021/match/1
```

for starting a Match's event stream 

```
curl -X GET http://localhost:2021/match/1/stream
```

for posting a Match info for saving and feeding event streams

for getting a Match info 

```
curl -X POST \
  http://localhost:2021/match \
  -H 'Content-Type: application/json' \
  -d '{
    "match-id": 1,
    "name": "Barcelona - Getafe",
    "start-date": "2019-05-01T19:00:00",
    "status": "COMPLETED",
    "score": "1 - 2",
    "events": [
        {
            "minute": 1,
            "type": "GOAL",
            "team": "Barcelona",
            "player-name": "Lionel Messi"
        },
        {
            "minute": 45,
            "type": "RED",
            "team": "Real Madrid",
            "player-name": "Sergio Ramos"
        },
        {
            "minute": 75,
            "type": "GOAL",
            "team": "Real Madrid",
            "player-name": "Luka Modric"
        },
        {
            "minute": 78,
            "type": "YELLOW",
            "team": "Real Madrid",
            "player-name": "Luka Modric"
        }
    ]
}'
```
