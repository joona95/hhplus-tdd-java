# Java 에서의 동시성 제어

## 동시성 제어 방법

### 1) synchronized

```java
public class Example {

    private int count = 0;

    public synchronized void plus() {
        count++;
    }

    public void plus() {
        synchronized (this) {
            count++;
        }
    }
}
```

#### 장점

- synchronized 키워드 사용 시 Java에서 동기화 구현하는 기본적 방법인 모니터 락이 적용된다.
    - 모니터 락 : 객체 단위의 락
- 특정 메서드나 블록에 대해 하나의 스레드만 접근할 수 있도록 제한한다.
- 간단한 사용 방법으로 동기화 처리가 가능하다.

#### 단점

- 세밀하게 락을 관리하기는 어렵다.
- 하나의 스레드만 접근할 수 있어 경쟁이 심해지면 속도가 느려진다.
- 여러 개의 synchronized 블록을 사용할 경우 데드락 발생이 가능하다.

### 2) Atomic

```java
import java.util.concurrent.atomic.AtomicInteger;

public class Example {

    private AtomicInteger count = new AtomicInteger(0);

    public void plus() {
        count.incrementAndGet();
    }
}
```

#### 장점

- 락 없이 동기화를 보장해야 할 때 사용할 수 있다.
- 락을 사용하지 않아 멀티스레드 환경에서 높은 성능을 제공한다.
- 내부적으로 CAS(비교 후 교체) 기법을 사용한다.
    - CAS 방식 : 공유 변수의 현재 값과 기대 값이 일치하면 새로운 값으로 갱신하는 방식

#### 단점

- 복잡한 연산을 수행할 수는 없다.
- CAS 방식은 ABA 문제가 발생할 수 있다.
    - ABA 문제 : CAS 연산 중간에 다른 스레드가 값 변경했다가 다시 값 돌려놓으면 갑지하지 못함

### 3) ConcurrentHashMap

```java
import java.util.concurrent.ConcurrentHashMap;

public class Example {

    private final ConcurrentHashMap<String, Integer> amp = new ConcurrentHashMap<>();

    public void put(String key, Integer value) {
        map.put(key, value);
    }
}
```

#### 장점

- 멀티스레드 환경에서 스레드 세이프하게 구현된 동시성 컬렉션이다.
- 전체적인 락을 걸지 않고 각 버킷 또는 키 단위로 동기화하여 성능을 최적화한다.
- 동시 읽기는 락 없이 가능하고 쓰기 연산도 최소한의 락만 사용한다.
- null 값을 허용하지 않아 안정성을 보장한다.

#### 단점

- 쓰기 연산에서는 내부적으로 락을 사용하여 동기화 수행하여 성능 저하될 수 있다.
- 읽기와 쓰기가 동시에 수행되는 경우, 읽기가 데이터 일관성 보장하지 않을 수 있다.
  - 따라서 데이터 일관성 보장해야 한다면 synchronized나 ReentrantLock을 추가로 고려해야 한다.


### 4) ReentrantLock

```java
import java.util.concurrent.locks.ReentrantLock;

public class Example {

    private int count = 0;
    private final ReentrantLock lock = new ReentrantLock();

    public void plus() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }

    public void plusTryLock() {
        if (lock.tryLock()) {
            try {
                count++;
            } finally {
                lock.unlock();
            }
        }
    }
}
```

#### 장점

- 락을 명시적으로 획득 또는 해제할 수 있다.
- synchronized 보다 세밀한 제어가 가능하여 유연한 동기화 기능을 제공한다.
- tryLock() 을 사용하면 락 획득 여부 확인이 가능하고 데드락 방지하거나 타임아웃 설정할 수 있다.

#### 단점

- synchronized 보다 코드가 복잡해질 수 있다.
- 직접 lock, unlock 호출을 해야 하므로 실수가 발생할 수 있다.


## 결론

### 프로젝트에서 필요한 동시성 제어 기능

- 포인트 조회, 포인트 내역 조회에서는 딱히 동시성 제어가 필요하지는 않아 보인다.
- 포인트 충전과 사용 작업이 이루어질 때 동시성 제어가 필요해 보인다.
- 한 유저에 포인트 충전/사용 요청이 동시에 들어왔을 때 요청들이 모두 정상적으로 반영되어야 한다.

### 프로젝트에서 사용할 동시성 제어

#### ReentrantLock + ConcurrentHashMap

- 특정 userId 값에 따라 lock 을 걸 필요가 있어서 ConcurrentHashMap 사용
- 읽기와 쓰기에 대한 데이터 일관성이 보장돼야 하므로 ReentrantLock 과 함께 사용

