---
layout: post
title: "도메인 주도 개발 시작하기 - 1장. 도메인 모델 시작하기"
categories: [software]
subcategory: ddd
tags: [domain-driven-design, DDD, software-architecture]
---

> "도메인 주도 개발 시작하기" 책을 읽으면서 핵심 내용을 정리해보자.

## 1장 도메인 모델 시작하기

"Garbage in, Garbage out"
> 사례: 
클라이언트의 기능 요구사항 : "현재 목록에 있는 전체 설문을 한번에 선택하고 반려하는 기능"
제안한 기능: 1주일이 지난 설문 중에서 승인하지 않은 설문을 자동으로 반려 처리
효과 : 클라이언트 업무 부담 감소

> 전문가나 관련자가 요구한 내용이 항상 올바른 것(정답)은 아니며 때론 본인들이 실제로 원하는 것을 정확하게 표현하지 못할 때도 있다. 그래서 개발자는 요구사항을 이해할 때 왜 이런 기능을 요구하는지 또는 실제로 원하는 게 무엇인지 생각하고 **전문가와 대화를 통해 진짜로 원하는 것을 찾아야 한다.**

### 1.4 도메인 모델 패턴

표 - 아키텍처 구성

|영역|설명|
|-|-|
|사용자 인터페이스 또는 표현|사용자의 요청을 처리하고 사용자에게 정보를 보여준다. 여기서 사용자는 소프트웨어를 사용하는 사람뿐만 아니라 외부 시스템일 수도 있다.|
|응용(Application)|사용자가 요청한 기능을 실행한다. 업무 로직을 직접 구현하지 않으며 도메인 계층을 조합해서 기능을 실행한다.|
|도메인|시스템이 제공할 도메인 규칙을 구현한다.|
|인프라스트럭처|데이터베이스나 메시징 시스템과 같은 외부 시스템과의 연동을 처리한다.|

> 도메인 계층은 도메인의 핵심 규칙을 구현한다. 주문 도메인의 경우 '출고 전에 배송지를 변경할 수 있다'라는 규칙과 '주문 취소는 배송 전에만 할 수 있다'라는 규칙을 구현한 코드가 도메인 규칙에 위치하게 된다. 이런 도메인 규칙을 객체 지향 기법으로 구현하는 패턴이 도메인 모델 패턴이다.

```java
public class Order {
    private OrderState state;
    private ShippingInfo shippinginfo;

    public void changeShippingInfo(ShippingInfo newShippingInfo) {
        if (!state.isShippingChangeable()) {
            throw new IllegalStateException("can't change shipping in " + state);
        }
        this.shippingInfo = newShippingInfo;
    }
    ...

}

public enum OrderState {
    PAYMENT_WAITING {
        public boolean isShippingChangeable() {
            return true;
        }
    },
    PREPARING {
        public boolean isShippingChangeable() {
            return true;
        }
    },
    SHIPPED, DELIVERING, DELIVERY_COMPLETED;

    public boolean isShippingChangeable() {
        return false;
    }
}
```

- 실제 배송지 정보를 변경하는 `Order` 클래스의 changeShippingInfo() 메서드는 OrderState의 isShippingChangeable() 메서드를 이용해서 변경 가능한 경우에만 배송지를 변경한다.

- 큰 틀에서 보면 OrderState는 Order에 속한 데이터이므로 배송지 정보 변경 가능 여부를 판단하는 코드를 Order로 이동할 수도 있다. 

```java
public class Order {
    private OrderState state;
    private ShippingInfo shippinginfo;

    public void changeShippingInfo(ShippingInfo newShippingInfo) {
        if (!isShippingChangeable()) {
            throw new IllegalStateException("can't change shipping in " + state);
        }
        this.shippingInfo = newShippingInfo;
    }

    private boolean isShippingChangeable() {
        return state == OrderState.PAYMENT_WAITING || state == OrderState.PREPARING;
    }
    ...
}

public enum OrderState {
    PAYMENT_WAITING, PREPARING, SHIPPED, DELIVERING, DELIVERY_COMPLETED;
}
```

- 배송지 변경이 가능한지를 판단할 규칙이 주문 상태와 다른 정보를 함께 사용한다면 `OrderState`만으로는 배송지 변경 가능 여부를 판단할 수 없으므로 `Order`에 로직을 구현해야 한다!
- 배송지 변경 가능 여부를 판단하는 기능이 `Order`에 있든 `OrderState`에 있든 중요한 점은 **주문과 관련된 중요 업무 규칙을 주문 도메인 모델인 `Order`나 `OrderState`에서 구현한다** 는 점이다. 
- 핵심 규칙을 구현한 코드는 도메인 모델에만 위치하기 때문에 규칙이 바뀌거나 규칙을 확장해야 할 때 다른 코드에 영향을 덜 주고 변경 내역을 모델에 반영할 수 있게 된다.

### 1.5 도메인 모델 도출

```java
public class Order {
    public void changeShipped() { ... }
    public void changeShippingInfo(ShippingInfo newShipping) {...}
    public void cancel() {...}
    public void completePayment() {...}
}

public class OrderLine {
    private Product product
    private int price;
    private int quantity;
    private int amounts;

    public OrderLine(Product product, int price, int quantity) {
        this.product = product;
        this.price = price;
        this.quantity = quantity;
    }

    private int calculateAmount() {
        return price * quantity;
    }
    
    private int getAmounts() {...}
    ///
}
```

여기서 `OrderLine`이란 주문에 포함된 개별 상품 항목(주문서의 한 줄)이라고 이해하자.
다음 요구사항은 `Order`와 `OrderLine`과의 관계를 알려준다.
- 최소 한 종류 이상의 상품을 주문해야 한다.
- 총 주문 금액은 각 상품의 구매 가격 합을 모두 더한 금액이다.

> 한 종류 이상의 상품을 주문할 수 있으므로 `Order`는 최소 한 개 이상의 `OrderLine`을 포함해야 한다. 또한 총 주문 금액은 `OrderLine`에서 구할 수 있다. 두 요구사항은 `Order`에 다음과 같이 반영할 수 있다.

```java
public class Order {
    private List<OrderLine> orderLines;
    private Money totalAmounts;

    public Order(List<OrderLine> orderLines) {
        setOrderLines(orderLines);
    }

    private set setOrderLines(List<OrderLines> orderLines) {
        verifyAtLeastOneOrMoreOrderLines(orderLines);
        this.orderLines = orderLines;
        calculateTotalAmounts();
    }

    private void verifyAtLeastOneOrMoreOrderLines(List<OrderLine> orderLines) {
        if (orderLines == null || orderLines.isEmpty()) {
            throw new IllegalArgumentException("no OrderLine");
        }
    }

    private void calculateTotalAmounts() {
        int sum = orderLines.stream()
                            .mapToInt(x -> x.getAmounts())
                            .sum()
        this.totalAmounts = new Money(sum);
    }
}
```

- '주문할 때 배송지 정보를 반드시 지정해야 한다'라는 내용이 있다.
- 이는 `Order`를 생성할 때 `OrderLine`의 `List` 뿐만 아니라 `ShippingInfo`도 함께 전달해야 함을 의미한다.

```java
public class Order {
    private List<OrderLine> orderLines;
    private ShippingInfo shippingInfo;
    ...

    public Order(List<OrderLine> orderLines, ShippingInfo shippingInfo) {
        setOrderLines(orderlines);
        setShippingInfo(shoppingInfo);
    }

    private void setShippingInfo(ShippingInfo shippingInfo) {
        if (shippingInfo == null) {
            throw new IllegalArgumentException("no shipping");
        }
        this.shippingInfo = shippingInfo;
    }
```

- 이렇게 함으로써 '배송지 정보 필수'라는 도메인 규칙을 구현한다.
- 도메인을 구현하다 보면 특정 조건이나 상태에 따라 제약이나 규칙이 달리 적용되는 경우가 많다.(진짜 많다...)
- 주문 요구사항에서는 다음 내용이 제약과 규칙에 해당된다.
    - 출고를 하면 배송지 정보를 변경할 수 없다.
    - 출고 전에 주문을 취소할 수 있다.
- 출고 상태에 따라 배송지 정보 변경 기능과 주문 취소 기능은 다른 제약을 갖는다.
- 이 요구사항을 충족하려면 주문은 최소한 출고 상태를 표현할 수 있어야 한다.
- "고객이 결제를 완료하기 전에는 상품을 준비하지 않는다"도 상태와 관련 있다.
- 이 요구사항은 결제 완료 전을 의미하는 상태와 결제 완료 내지 상품 준비 중이라는 상태가 필요함을 알려준다.

```java
public enum OrderState {
    PAYMENT_WAITING, PREPARING, SHIPPED, DELIVERING, DELIVERY_COMPLETED, CANCELED;
}
```

- 배송지 변경이나 주문 취소 기능은 출고 전에만 가능하다는 제약 규칙이 있으므로 이 규칙을 적용하기 위해 `changeShippingInfo()`와 `cancel()`은 `verifyNotYetShipped()` 메서드를 먼저 실행한다.

```java
public class Order {
    private OrderState state;

    public void changeShippingInfo(ShippingInfo newShippingInfo) {
        verifyNotYetShipped();
        setShippingInfo(newShippingInfo);
    }
}
```

### 1.6 엔티티와 밸류

> 도출한 모델은 크게 엔티티(Entity)와 밸류(Value)로 구분할 수 있다. 
엔티티와 밸류를 제대로 구분해야 도메인을 올바르게 설계하고 구현할 수 있기 때문에 이 둘의 차이를 명확하게 이해하는 것은 도메인을 구현하는 데 있어 중요하다.

#### 1.6.1 엔티티

> 엔티티의 가장 큰 특징은 식별자를 가진다는 것이다. 식별자는 엔티티 객체마다 고유해서 각 엔티티는 서로 다른 식별자를 갖는다. 예를 들어 주문 도메인에서는 주문번호를 식별자로 가지는 주문이 엔티티가 된다. 

- 엔티티의 식별자는 고유하므로 두 엔티티 객체의 식별자가 같으면 두 엔티티는 같다고 판단할 수 있다.
- 엔티티를 구현한 클래스는 다음과 같이 식별자를 이용해서 `equals()` 메서드와 `hashCode()` 메서드를 구현할 수 있다.

```java
public class Order {
    private String orderNumber;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj.getClass() != Order.class) return false;
        Order other = (Order)obj;
        if (this.orderNumber == null) return false;
        return this.orderNumber.equals(other.orderNumber);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((orderNumber == null) ? 0 : orderNumber.hashCode());
        return result;
    }
}
```

> **`hashCode()`는 고유한 값일까?**
> - 아니다. `HashMap`/`HashSet`이 객체를 찾을 버킷을 정할 때 쓰는 `int` 값이다. 즉, 나올 수 있는 값이 약 43억 개 뿐. 문자열은 무한히 만들 수 있으니, 서로 다른 문자열이 같은 번호를 받는 경우가 반드시 생김.
>   - 해시값이 무작위로 고르게 나온다고 가정하면, 약 7만 7천 개만 만들어도 어딘가 한 쌍이 겹칠 확률이 50%임.
> - 서로 다른 객체도 같은 값을 가질 수 있다(해시 충돌). 예: `"Aa"`, `"BB"` → 2112
> - `equals()`가 true면 `hashCode()`는 반드시 같아야 한다. 역은 성립하지 않아도 된다.
> - 해시 분포가 고르다 = 객체가 특정 버킷에 몰리지 않는다 → 비교 횟수가 줄어든다.

```java
"Aa".hashCode()  // 'A'(65) * 31 + 'a'(97) = 2112
"BB".hashCode()  // 'B'(66) * 31 + 'B'(66) = 2112
```

> "해시 분포를 고르게 만든다?"
> `hashMap` 은 서랍장이라고 생각하자. 처음엔 보통 서랍이 16칸이고, 데이터가 많아지면 늘어난다. 값을 찾는 순서는 다음과 같다.
1. `hashCode()`로 몇 번 서랍인지 계산한다.
2. 그 서랍 안의 후보들만 비교해서 `equals()` 로 진짜인지 확인한다.

```text
고르게 분포                한쪽에 몰림
[0] A                     [0]
[1] B                     [1]
[2] C                     [2] A, B, C, D
[3] D                     [3]
→ 서랍 열면 바로 찾음       → 서랍 안을 또 뒤져야 함
```

> **왜 31을 곱할까?**
> - 곱셈: 필드의 순서를 결과에 반영한다. (`31*1+2 ≠ 31*2+1`)
> - 홀수: 짝수를 곱하면 오버플로 시 상위 비트의 정보가 점점 사라진다.
> - 소수: 『이펙티브 자바』에 따르면 이점은 불분명하며 관례에 가깝다.
> - 최적화: `31 * i == (i << 5) - i`이며, JVM이 자동으로 최적화할 수 있다.

① 왜 곱하나? → 순서를 반영하려고

필드가 두 개라고 해보자.

| 필드 값 | 그냥 더하기 `a + b` | `31 * a + b` |
|---|---|---|
| (1, 2) | 3 | 33 |
| (2, 1) | **3** (충돌) | 63 |

숫자 12와 21이 다른 것처럼, 곱셈이 **자리마다 다른 가중치**를 준다.

곱하는 수가 너무 작아도 자리끼리 값이 쉽게 겹친다. `31`은 소문자 알파벳 26개보다 커서 소문자 두 글자 조합 676개는 모두 다른 값이 나온다. 
반면 `'a'(97) - 'B'(66) = 31`이라서 대소문자가 섞인 `"Aa"`와 `"BB"`는 겹친다.

② 왜 홀수인가? → 정보가 밀려 사라지지 않게

**끝 3자리만 보여주는 계산기**를 상상해 보자. 123에 10을 계속 곱하면 다음과 같다.

```
123 → 230 → 300 → 000     // 원래 숫자의 흔적이 사라짐
```

`int`는 **2진수의 끝 32자리만 보관하는 계산기** 라고 할 수 있다. 
그래서 10진수에서 10을 곱할 때처럼, 2진수에서 짝수를 곱하면 정보가 위로 밀려 잘려 나간다. 
31 대신 32를 곱하면 다음과 같이 된다.

```java
// 곱하는 수를 32로 바꾼 가상의 해시
"XXXXabcdefg" → 174301479
"YYabcdefg"   → 174301479   // 앞부분이 달라도 같음
```

32를 곱할 때마다 2진수로 5칸씩 밀린다. 그래서 **뒤의 7글자만 결과에 영향을 주게된다.** 31로 계산하면 위 두 문자열은 다른 값이 나온다.

홀수를 곱하면 32비트 안에서 **곱하기 전 값이 다르면 곱한 후 값도 다르다.** 곱셈 때문에 정보가 사라지지는 않는다는 뜻. 다만 이후에 다음 필드 값을 더한 최종 결과는 여전히 겹칠 수 있다.


> **`result = 1`의 의미**
> - 0으로 시작하면 앞쪽의 해시값 0인 요소가 결과에 흔적을 남기지 않는다. (`[5]`, `[0, 5]` → 모두 5)
> - 요소 개수가 달라지는 경우에는 의미가 있다. (`List.hashCode()`는 1로 시작)
> - 필드 개수가 고정된 클래스에서는 모든 결과에 같은 상수가 더해질 뿐, `hashCode` 충돌은 달라지지 않는다.
> - 위 코드의 결과는 `Objects.hash(orderNumber)`와 같다.
> - 참고: 『이펙티브 자바』 2판은 0이 아닌 상수(예: 17), 3판은 첫 핵심 필드의 해시값으로 초기화한다.

#### 1.6.2 엔티티의 식별자 생성
> 엔티티의 식별자를 생성하는 시점은 도메인의 특징과 기술에 따라 달라진다. 흔히 식별자는 다음 한 가지 방식으로 생성한다.

- 특정 규칙에 따라 생성
- UUID나 Nano ID와 같은 고유 식별자 생성기 사용
- 값을 직접 입력
- 일련번호 사용(시퀀스나 DB의 자동 증가 컬럼 사용)

> 자동 증가 컬럼을 제외한 다른 방식은 다음과 같이 식별자를 먼저 만들고 엔티티 객체를 생성할 때 식별자를 전달한다.

```java
// 엔티티를 생성하기 전에 식별자 생성
String orderNumber = orderRepository.generatorOrderNumber();

Order order = new Order(orderNumber, ...);
orderRepository.save(order);
```

> 자동 증가 컬럼은 DB 테이블에 데이터를 삽입해야 비로소 값을 알 수 있기 때문에 테이블에 데이터를 추가하기 전에는 식별자를 알 수 없다. 이것은 엔티티 객체를 생성할 때 식별자를 전달할 수 없음을 의미한다.

```java
Article article = new Article(author, title, ...);
articleRepository.save(article); // DB에 저장한 뒤 구한 식별자를 엔티티에 반영
Long saveArticleId = article.getId(); // DB에 저장한 후 식별자 참조 가능
```

#### 1.6.3 밸류 타입

`ShippingInfo` 클래스는 받는 사람과 주소에 대한 데이터를 갖고 있다.

```java
public class ShippingInfo {
    private String receiverName;
    private String receiverPhoneNumber;

    private String shippingAddress1;
    private String shippingAddress2;
    private String shippingZipcode;
}
```

밸류 타입은 개념적으로 완전한 하나를 표현할 때 사용한다. 예를 들어 받는 사람을 위한 밸류 타입인 `Receiver`를 다음과 같이 작성할 수 있다.

```java
public class Receiver {
    private String name;
    private String phoneNumber;

    public Receiver(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
```

마찬가지로 `ShippingInfo`의 주소 관련 데이터도 `Address` 밸류 타입을 사용해서 보다 명확하게 표현할 수 있다.

```java
public class ShippingInfo {
    private Receiver receiver;
    private Address address;
}
```

밸류 타입의 또 다른 장점은 밸류 타입을 위한 기능을 추가할 수 있다는 것이다. 예를 들어 Money 타입은 다음과 같이 돈 계산을 위한 기능을 추가할 수 있다.

```java
public class Money {
    private int value;

    public Money add(Money money) {
        return new Money(this.value + money.value);
    }

    public Money multiply(int multiplier) {
        return new Money(this.value * multiplier);
    }
}
```

> Money를 사용하는 코드는 이제 '정수 타입 연산'이 아니라 '돈 계산'이라는 의미로 코드를 작성할 수 있다.