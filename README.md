# jwp-subway-path

## 요구사항

### API 요구사항
- [x] 노선 추가
- [ ] 노선 조회
    - [ ] 노선에 포함된 역을 순서대로 보여주도록 한다.
- [ ] 노선 목록 조회
    - [ ] 노선에 포함된 역을 순서대로 보여주도록 한다.
- [ ] 역 추가
  - [ ] 역이 등록될 때 양의 정수인 거리 정보가 포함되어야 한다.
  - [ ] 노선에 역이 하나도 없을 때 두 역을 동시에 등록해야 한다.
  - [ ] 하나의 역은 여러 개의 노선에 등록될 수 있다.
  - [ ] 존재하는 역의 다음 역을 추가하면 중간에 역이 등록될 수 있다.
  - [ ] 노선 사이에 역이 등록되는 경우 등록된 역을 포함한 전 역, 다음 역의 거리를 업데이트 한다.
  - [ ] 노선 사이에 역이 등록되는 경우 새로 추가되는 역과 이전 역의 거리는 기존 역 사이의 거리보다 작아야 한다.
  - [ ] 기준이 되는 역이 없으면 등록할 수 없다.
- [ ] 역 조회
- [ ] 역 목록 조회
- [ ] 역 삭제
  - [ ] 역을 제거한 후 노선을 재배치한다.
  - [ ] 역을 제거한 후 제거된 역의 전 역, 다음 역의 거리를 업데이트한다.
  - [ ] 역을 제거한 후 노선에 역이 하나 남으면 해당 역도 제거한다.

## 도메인

### 지하철(Subway)

- [x] 역과 거리 정보를 저장한다.

### 역(Station)

- [x] 이름, 노선 리스트를 갖는다.

### 거리정보(Edge)

- [x] 이전 역, 다음 역, 거리, 노선을 갖는다.

### 노선(Line)

- [x] 이름을 갖는다.

## API 명세서

### 노선 API

| Method | URI         | Description |
|--------|-------------|-------------|
| POST   | /lines      | 노선 추가       |
| GET    | /lines      | 전체 노선과 역 조회 |
| GET    | /lines/{id} | 특정 노선과 역 조회 |

### 역 API

| Method | URI            | Description |
|--------|----------------|-------------|
| POST   | /stations      | 역 추가        |
| GET    | /stations      | 전체 역 조회     |
| GET    | /stations/{id} | 특정 역 조회     |
| DELETE | /stations/{id} | 특정 역 삭제     |

## Line API 요청 / 응답 예시
### POST : 노선 추가
#### Request
```http request
POST /lines HTTP/1.1
Host: localhost:8080

{
    "name" : "1호선"
}
```

#### Response
``` http request
HTTP/1.1 201 Created
Content-Type: application/json
Location: /lines/1
```
### GET : 노선 목록 조회
#### Request
```http request
GET /lines HTTP/1.1
Host: localhost:8080
```

#### Response
``` http request
HTTP/1.1 200
Content-Type: application/json

[
    {
        "name" : "2호선"
        "stations" : [
            {
                "name" : "잠실역"
            }
        ]
    },
    {
        "name" : "8호선"
        "stations" : [
            {
                "name" : "잠실역"
            },
            {
                "name" : "석촌역"
            }
        ]
    }
]
```

### GET : 노선 조회
#### Request
```http request
GET /lines/{id} HTTP/1.1
Host: localhost:8080
```

#### Response
``` http request
HTTP/1.1 200
Content-Type: application/json

{
    "name" : "2호선"
    "stations" : [
        {
            "name" : "잠실역"
        }
    ]
}
```

---

## Station API 요청 / 응답 예시

### POST : 역 추가
#### Request
```http request
POST /stations HTTP/1.1
Host: localhost:8080

{
    "startStation" : "잠실역",
    "endStation" : "잠실새내역",
    "distance" : 10,
    "line" : 1
}
```

#### Response
``` http request
HTTP/1.1 201 Created
Content-Type: application/json
Location: /stations/1
```

### GET : 역 목록 조회
#### Request
```http request
GET /stations HTTP/1.1
Host: localhost:8080
```

#### Response
``` http request
HTTP/1.1 200
Content-Type: application/json

[
    {
        "name" : "잠실역"
        "lines" : [
            {
                "name" : "2호선"
            },
            {
                "name" : "8호선"
            }
        ]
    },
    {
        "name" : "잠실새내역"
        "lines" : [
            {
                "name" : "2호선"
            }
        ]
    }
]
```

### GET : 특정 역 조회
#### Request
```http request
GET /stations/{id} HTTP/1.1
Host: localhost:8080
```

#### Response
``` http request
HTTP/1.1 200
Content-Type: application/json

{
    "name" : "잠실역"
    "lines" : [
        {
            "name" : "2호선"
        },
        {
            "name" : "8호선"
        }
    ]
}
```

### DELETE : 특정 역 삭제
#### Request
```http request
DELETE /stations/{id} HTTP/1.1
Host: localhost:8080
```

#### Response
``` http request
HTTP/1.1 204 No Content
```
