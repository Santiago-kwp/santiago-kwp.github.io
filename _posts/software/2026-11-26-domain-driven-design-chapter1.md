---
layout: post
title: "도메인 주도 개발 시작하기 - 1장. 도메인 모델 시작하기"
categories: [software]
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

여기서 OrderLine이란 주문에 포함된 개별 상품 항목(주문서의 한 줄)이라고 이해하자.
다음 요구사항은 Order와 OrderLine과의 관계를 알려준다.
- 최소 한 종류 이상의 상품을 주문해야 한다.
- 총 주문 금액은 각 상품의 구매 가격 합을 모두 더한 금액이다.

> 한 종류 이상의 상품을 주문할 수 있으므로 Order는 최소 한 개 이상의 OrderLine을 포함해야 한다. 또한 총 주문 금액은 OrderLine에서 구할 수 있다. 두 요구사항은 Order에 다음과 같이 반영할 수 있다.

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
- 이는 Order를 생성할 때 OrderLine의 List 뿐만 아니라 ShippingInfo도 함께 전달해야 함을 의미한다.

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