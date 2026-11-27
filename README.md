# 콩돈 가계부 (Android 네이티브)

가계부 앱 **콩돈**의 Kotlin + Jetpack Compose 버전입니다.
같은 기획의 Ionic 버전은 `ionic/household-ledger`, 백엔드는 `nest/household-ledger-api`입니다.

## 핵심 기능

- **알림 기반 자동 수집:** 알림 접근 권한으로 카드, 은행, 페이의 결제·입출금 알림을 모읍니다. 알림 원문은 `RawEvent`, 파싱 결과는 거래 후보로 저장합니다.
- **검토함(Inbox):** 자동으로 저장하지 않고, 사용자가 검토함에서 확정해야 거래가 됩니다. 같은 거래가 여러 번 알림으로 와도 중복되지 않게 막습니다.
- **취소 매칭:** 승인 취소 알림이 오면 시간, 금액, 가맹점, 카드사를 비교해서 기존 거래와 연결합니다. 애매하면 검토함에 취소 후보로 보여 줍니다.
- **AI 자연어 입력:** 홈 상단 입력창에 "아이폰 구매 155만원"처럼 쓰면 금액, 가맹점, 카테고리, 수입/지출을 자동으로 분류합니다.
- **화면:** 하단 탭 4개(홈, 캘린더, 검토함, 설정)

## 기술 스택

- Kotlin, Jetpack Compose, Room
- 기능 단위 패키지(`feature/<기능>/ui·domain·data`)와 공용 `core`

전체 요구사항은 [`REQUIREMENTS.md`](REQUIREMENTS.md), 작업 규칙은 [`CLAUDE.md`](CLAUDE.md)에 있습니다.

## 주의

릴리스 서명 키(`app/keystores/*.keystore`)와 `local.properties`는 `.gitignore`로 제외되어 있습니다.
