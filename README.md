# concurrency-problem-solving
동시성 문제 해결 과정 with Redis, kafka

step 1: 데이터베이스 하나로 이 문제를 해결하기(데이터베이스의 기능을 활용) -> 데이터베이스에 많은 부담이 된다.. -> 사용자가 증가할수록 데이터베이스에 CPU 터짐.. 60% & 서버에 과부하
step 1 answer: database lock & mvcc

step 2: 데이터베이스의 기능을 -> 외부 로직으로 분산시킨다 -> 데이터베이스의 부담을 줄이는 과정.. & 서버에 과부하
step 2 answer: 레디스의 분산락(redisson) with spring boot

step 3: MSA 상황에서 어떻게 처리해야 하는가 -> 서버의 부담을 줄이는 과정 & 데이터베이스의 과부하를 줄이는 것..
step 3 answer: 순서 보장은 레디스 & 카프카를 활용해서 분산처리

step 4: 컨트롤러에서 수량 관리(서비스 단 부담 낮추기)

other: 치킨 이벤트를 한다. step3까지 해도 터질 우려가 있음.. 대기열(redis)
