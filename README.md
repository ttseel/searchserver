
# 목차
- [빌드 결과물 링크](#빌드-결과물-링크)
- [개발 환경](#개발-환경)
- [기능 요구사항 및 제약사항](#기능-요구사항-및-제약사항)
- [API 명세](#API-명세)
- [문제 해결 전략 및 고려사항](#문제-해결-전략-및-고려사항)
</br>

# 빌드 결과물
### 링크
https://github.com/ttseel/searchserver/blob/master/searchserver.jar

### 실행방법
```
$ java -jar searchserver.jar
```

</br>

# 개발 환경
|Java | openjdk 11.0.9
|-----|---------------


### Dependencies
|Dependency         |Version | Description
|-------------------|--------|------------------------
|spring-boot        |2.7.9   | 간단한 설정으로 Spring 어플리케이션 구축
|Spring-data-jpa,<br> hibernate    |2.7.8   | Java ORM 기술을 이용해 DB 컨트롤
|Srping-web         |5.3.25  | HTTP 요청 및 응답 처리, REST API 구현
|lombok             |1.18.26 | 코드 가독성 및 개발 생산성
|h2database         |2.1.214 | 데이터 저장용 In-Memory DB 

</br>

# 기능 요구사항 및 제약사항

<details>
<summary>기능 요구사항</summary>
    
    1. 블로그 검색
    - 키워드를 통해 블로그를 검색할 수 있어야 합니다.
    - 검색 결과에서 Sorting(정확도순, 최신순) 기능을 지원해야 합니다.
    - 검색 결과는 Pagination 형태로 제공해야 합니다.
    - 검색 소스는 카카오 API의 키워드로 블로그 검색 (https://developers.kakao.com/docs/latest/ko/daum-search/dev-guide#search-blog)을 활용합니다.
    - 추후 카카오 API 이외에 새로운 검색 소스가 추가될 수 있음을 고려해야 합니다.

    2. 인기 검색어 목록
    - 사용자들이 많이 검색한 순서대로, 최대 10개의 검색 키워드를 제공합니다.
    - 검색어 별로 검색된 횟수도 함께 표기해 주세요.
    - 주어진 요구사항 이외의 추가 기능 구현에 대한 제약은 없으며, 새롭게 구현한 기능이 있을 경우 README 파일에 기재 바랍니다.
</details>

<details>
<summary>제약사항</summary>

    - JAVA 11 이상 또는 Kotlin 사용
    - Spring Boot 사용
    - Gradle 기반의 프로젝트
    - 블로그 검색 API는 서버(백엔드)에서 연동 처리
    - DB는 인메모리 DB(예: h2)를 사용하며 DB 컨트롤은 JPA로 구현
    - 외부 라이브러리 및 오픈소스 사용 가능 (단, README 파일에 사용한 오픈 소스와 사용 목적을 명시해 주세요)
</details>

</br>

# API 명세
## 1. 검색하기
- 사용자가 입력한 키워드로 카카오 API 블로그 검색 결과를 제공합니다.
- 검색 결과는 정확도순이나 최신순으로 Sorting할 수 있으며, Pagination 방식으로 확인 가능합니다.
- 만약 카카오 API에 장애가 발생하면 네이버 블로그 검색 API를 사용하여 결과를 제공합니다.

### 1) 기본 정보
```bash
GET /search/blog HTTP
Host: localhost:8080
```

### 2) Request
#### Parameter

|Name      |Type    |Description             |Required
|----------|--------|------------------------|---------------------
|`query`   |String  |검색을 원하는 질의어         |O  
|`sort`    |String  |결과 문서 정렬 방식, accuracy(정확도순) 또는 recency(최신순), 기본 값 accuracy |X
|`page`    |Integer |결과 페이지 번호, 1~50 사이의 값, 기본 값 1   |X
|`size`    |Integer |한 페이지에 보여질 문서 수, 1~50 사이의 값, 기본 값 10    |X

### 3) Response
#### metaInfo
|Name            |Type    |Description
|----------------|--------|------------------------
|`total_count`   |Integer |검색된 문서 수
|`pageable_count`|Integer |total_count 중 노출 가능 문서 수
|`is_end`        |Boolean |현재 페이지가 마지막 페이지인지 여부, 값이 false면 page를 증가시켜 다음 페이지를 요청할 수 있음

#### postingList
Name            |Type    |Description
|----------------|--------|------------------------
|`title`         |String  |문서 제목
|`contents`      |String  |문서 본문 중 일부
|`url`           |String  |문서 URL
|`datetime`      |Datetime|문서 글 작성시간, ISO 8601<br/>[YYYY]-[MM]-[DD]T[hh]:[mm]:[ss].000+[tz]

### 4) Sample
#### Request
```bash
https://openapi.naver.com/v1/search/blog.json?query=주담대
```

#### Response
```bash
{
    "metaInfo": {
        "sort": accuracy,
        "page": 27,
        "size": 40
        "totalCount": 167854,        
    },
    "postingList": [
        {
            "title": "다주택자 <b>주담대</b> 규제 풀린다.",
            "contents": "적용이 되었으나 규제지역에서는 ltv가 0%로 대출이 막혀있어서 용산, 강남,서초,송파구에서는 대출이 원칙적으로 불가했습니다. 하지만 이번 규제지역내 <b>주담대</b> 허용에 따라 30% ltv를 적용받을 수 있게 되었습니다. 1주택자가 추가로 규제지역내 주택을 구매할때 집값의 30%를 대출을 받을 수 있다는 이야기인데 (물론...",
            "url": "http://economydaddy.tistory.com/218",
            "blogName": null,
            "thumbnail": "https://search1.kakaocdn.net/argon/130x130_85_c/KFh5yaGaHsr",
            "dateTime": "2023-02-11T08:29:58"
        },
        ...
    ]
}
```
<br/>

## 2. 인기 검색어 목록
- 최대 10개의 검색 키워드와 조회수를, 사용자들이 많이 검색한 순서대로 제공합니다.
- `인기 검색어는 180초마다 갱신되며`, 마지막 업데이트 시간과 업데이트 주기는 updateTime과 updatePeriod로 확인 가능합니다.
- `(참고) 과제 확인시 편의를 위해 제출된 어플리케이션 세팅 값은 20초 입니다!`

### 1) 기본 정보
```bash
GET /search/top10 HTTP
Host: localhost:8080
```

### 2) Request
#### Parameter
|Name    |Type    |Description               |Required
|--------|--------|--------------------------|---------------------
|None    |None    |Parameter is not required |X

### 3) Response
|Name              |Type    |Description
|------------------|--------|------------------------
|`updateTime`      |Integer |마지막 업데이트 시간
|`updatePeriod`    |Integer |업데이트 주기
|`top10List`       |Array   |인기검색어 리스트(하단 추가 설명)

#### top10List
|Name              |Type    |Description
|------------------|--------|------------------------
|`rank`            |Integer |조회수 순위
|`keyword`         |Integer |검색 키워드
|`readCount`       |Integer |조회수

### 4) Sample
#### Request
```bash
http://localhost:8080/search/top10
```

#### Response
```bash
{
    "updateTime": "2023-03-22T18:17",
    "updatePeriod": "60sec",
    "top10List": [
        {
            "rank": 1,
            "keyword": "대출",
            "readCount": 21474184
        },
        {
            "rank": 2,
            "keyword": "신용",
            "readCount": 7123714
        },
        {
            "rank": 3,
            "keyword": "금리",
            "readCount": 5294512
        },
        ...
    ]
}
```

## 에러 코드 및 메시지
<details>
<summary>Error Code</summary>

### Common(공통) 에러 코드
| 에러코드           | 메시지                             |
| ------------------ | ---------------------------------- |
| 서버 내부 오류     |                                    |
| `CE101(500)`    | 예기치 못한 오류가 발생했습니다   |

### Business 에러 코드
| 에러코드           | 메시지                             |
| ------------------ | ---------------------------------- |
| 서버 내부 오류     |                                    |
| `BE101(500)`    | 인기검색어 업데이트에 실패했습니다   |
    
### Search 에러코드  
| 에러코드           | 메시지                             |
| ------------------ | ---------------------------------- |
| 사용자 요청 오류   |                                    |
| `SE101(400)`             | 잘못된 query 요청입니다              |
| `SE102(400)`             | 부적절한 sort 값 입니다           |
| `SE103(400)`             | 부적절한 page 값 입니다          |
| `SE104(400)`             | 부적절한 size 값 입니다          |
| 서버 내부 오류     |                                    |
| `SE111(500)`             | 알 수 없는 오류가 발생하였습니다   |
| 외부 요청 오류 |                                    |
| `SE112(503)`             | 검색 요청이 실패했습니다   |

</details>

</br>

# 문제 해결 전략 및 고려사항
## 1. 검색 소스 확장을 고려한 Chain of Reponsibility 패턴 적용
- 본 서비스는 추후 카카오 검색 API 이외에 새로운 검색 소스가 추가될 수 있음
- 또한, 카카오 블로그 검색 API에 장애 발생으로 실패하면 Naver 등의 타 API에서 검색 결과를 요청할 수 있어야 함
- 위 두 가지 조건을 해결하기 위해 Chain of Responsibility 패턴을 적용하여 개발함
  - 해당 패턴을 적용하면 기존 코드에 변경사항을 발생시키지 않으면서 새로운 검색 소스를 추가할 수 있음(OCP 준수)
  - Next Handler로 지정된 Naver 블로그 검색 API를 통해 데이터를 제공할 수 있음

![image](https://user-images.githubusercontent.com/66378928/226900113-3a357a42-bd24-4eb3-abf0-1c4672f2ff1b.png)

![image](https://user-images.githubusercontent.com/66378928/226899821-7a70b7b7-8e20-4946-a2ff-6dcdf6d3858c.png)





## 2. 대용량 트래픽 환경을 고려한 인기검색어 캐싱 및 주기적 업데이트(스케줄링)
### 2.1. 캐싱과 주기적 업데이트를 사용한 이유
- 본 서비스가 Daum 포탈과 같은 MAU 900만 수준의 서비스에서 제공한다고 가정할 때, 검색어를 조회할 때마다 수 억개의 키워드를 저장한 테이블에 Insert, Update 쿼리를 사용하는 것은 DB에 큰 부하를 줄 수 있어 부적절함
- 이를 해결하기 위해 Redis와 같은 Key-Value 데이터베이스를 고려할 수 있지만, DB 컨트롤은 JPA로 하는 제약사항으로 선택하지 않음
- 대신, 메모리에 각 키워드 별 검색 횟수를 저장하고, 180초를 주기로 키워드 별 조회수를 Batch 업데이트하는 전략을 사용함
- 업데이트 주기는 application.yml에서 schedule.top10.period 속성으로 변경할 수 있어 어플리케이션의 추가적인 빌드없이 적용할 수 있음

`application.yml`
```bash 
schedule:
  top10:
    period: 180000 # millisec
```

### 2.2. ConcurrentHashMap과 Copy를 이용해 동시성 문제 해결
- 동시성이 발생할 수 있는 부분은 180초를 주기로 Insert & Update를 완료 후 Clear를 하는 동안, 새로운 블로그 조회 요청이 들어와 다른 Thread에 의해 Hash 데이터가 변경되는 경우임
- 즉, 특정 키워드에 대한 조회 카운트가 유실될 가능성이 존재하여 이를 해결하기 위해 아래와 같은 절차로 진행함
  - DB에 변경사항을 반영하기 전, Thread-safe한 상태인 ForEach를 문에서 새로운 Hash로 Copy를 진행함과 동시에 Remove함
  - 새로운 Hash에서 데이터베이스로 배치 Insert, Update 처리

## 3. DB 최적화
### 3.1. Bulk Update 최적화
1) Insert 시 트랜잭션
2) Update 시 plus count가 동일한 키워드를 묶어 in 쿼리로 처리

### 3.2. 인덱스 추가
1) count 컬럼 내림차순 인덱스
1.1. 목적: 인기 검색어 요청 시 count 내림차순 Top10을 조회하므로 쿼리 성능 향상됨
1.2. 성능 향상 : Keyword 1억건 기준 쿼리시간 1/4으로 단축

2) keyword 컬럼 인덱스
1.1. 목적: update 시 keyword를 조건으로 update 되므로 쿼리 성능 향상
1.2. 성능 향상 : 

## 4. Filter를 이용한 HTTP Request / Reponse logging
- Filter는 Servlet 컨테이너에서 동작하므로, 모든 HTTP 요청 및 응답에 대한 로깅 처리 가능






