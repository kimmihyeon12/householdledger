# CLAUDE.md (Android 가계부 프로젝트용 명령/스펙 파일)
# 목적: 이 파일을 Claude Code / Cursor / Copilot Chat(참고용)에 붙여서 “프로젝트를 일관되게” 진행한다.

## 0) 프로젝트 개요
- 앱 이름(임시): AutoLedger
- 플랫폼: Android (Kotlin) + Jetpack Compose + Room
- 핵심 USP: 카드/은행/페이 결제·입출금 **알림 기반 자동 수집** → 사용자가 **확정(Inbox)** 하면 거래로 저장
- 유지율 장치: 거래 확정/정산 행동에 대해 **포인트 지급**
- AI 입력: 자연어로 거래를 입력하면 자동 분류 및 등록 (예: "아이폰 구매 155만원")
- 수익화(로드맵): **포인트 충전(현질) + 광고 제거(선택)**
  - ⚠️ **MVP에서는 결제/과금/구매내역/영수증/복원 기능을 구현하지 않는다.** (UI/DB 스키마만 최소 준비 가능)

## 1) 핵심 기능 요구사항
### 1.1 자동 수집(알림 기반)
- Notification Access 권한을 받아 결제/입출금 관련 알림을 수집한다.
- 알림 원문을 `RawEvent`로 저장한다(민감정보 최소화 원칙).
- 파싱 결과는 `ParsedTransactionCandidate`로 저장한다.
- 기본 플로우는 “자동 저장”이 아니라 **Inbox(검토함) → 사용자 확정** 방식이다.
- 중복 방지 필요(같은 알림 재전송/앱 재시작/동일 거래 여러 알림).

### 1.2 수동 입력
- 사용자가 직접 거래 추가 가능.
- 수동 입력도 “확정” 단계(또는 즉시 확정)로 처리 가능.

### 1.3 취소/부분취소 처리(가능 범위 내)
- “승인/취소” 패턴이 알림에 나타나면 기존 거래와 매칭해서 상태 업데이트.
- 매칭 키 우선순위: (timestamp 근접 + 금액 + 가맹점/기관명 + 카드사/은행명).
- 애매하면 Inbox에서 사용자에게 “취소 후보”로 표시하여 수동 확인.

### 1.4 포인트(가계부+게임화)
- 포인트는 "지출 자체"가 아니라 **정리/확정 행동**에 대해 지급한다.
- 지급 규칙(초안):
  - Inbox 거래 1건 확정: +2P
  - 하루 정산(확정 5건 이상): +10P
  - 주간 리포트 확인: +20P
  - 월 마감(월 단위 리포트 확정): +100P

### 1.5 AI 자연어 입력
- 사용자가 자연어로 거래를 입력하면 자동으로 금액, 가맹점, 카테고리를 분류한다.
- 입력 예시: "아이폰 구매 155만원", "스타벅스 아메리카노 4500원", "월급 320만원"
- 금액 파싱: "만원", "천원", 콤마 포함 숫자, 순수 숫자+원 등 다양한 형식 지원
- 수입/지출 자동 판별: "월급", "급여", "용돈" 등 키워드로 수입 판별, 나머지는 지출
- 카테고리 자동 매칭: 키워드 기반 분류 (식비/교통/쇼핑/여가/건강/교육/생활/기타)
- AI 분석 결과를 미리보기로 보여주고, 사용자가 확인 후 등록
- (추후) 서버 AI API 연동으로 분류 정확도 향상

## 2) UI/화면 구성(Compose)
- Main(Home): 월 요약(지출/수입/잔액) + 최근 거래 리스트 + "Inbox n건"
- Calendar: 월별 달력 뷰 + 날짜별 거래 조회/추가
  - 월 네비게이션(이전/다음 달 이동)
  - 상단 월 합계(수입/지출) 요약 칩
  - 달력 그리드: 날짜별 수입(파랑)/지출(빨강) 금액 축약 표시
  - 오늘 날짜 하이라이트(원형 배경)
  - 일요일 빨강 / 토요일 파랑 컬러
  - 날짜 선택 시: 해당 일자 거래 리스트 펼침 + 일자별 수입/지출 합계 카드
  - 선택 날짜에서 거래 추가 가능(날짜 자동 설정)
  - 거래 없는 날: "이 날은 거래가 없어요" 안내 + 거래 추가 버튼
- Inbox: 자동 수집된 후보 거래 리스트
  - 항목: 금액/가맹점/시간/출처(카드/은행/페이)/상태(승인/취소후보)
  - 액션: 확정, 수정(카테고리/메모), 삭제
- Add/Edit Transaction: 수동 입력/수정 화면
  - 캘린더에서 진입 시 선택한 날짜가 자동 설정됨
- Stats: 카테고리별/주간/월간 차트(단순)
- AI Input: 자연어 거래 입력 화면
  - 입력창에 텍스트 입력 → AI 파싱 → 결과 미리보기 카드 → 등록/취소
  - 예시 가이드 표시 (첫 화면)
  - 등록 히스토리 표시
- Point Wallet(포인트): 보유 포인트 + (추후) 충전 진입점
  - ⚠️ MVP에서는 **충전 버튼/결제 UI/구매내역 화면은 구현하지 않음**(placeholder 문구는 가능)
- Settings:
  - 알림 접근 권한 안내/ON 여부
  - 수집 대상 앱/키워드 필터(카드사/은행 선택)
  - 개인정보 처리 안내(필수)

### Bottom Navigation 구성 (5탭)
- 홈 | 캘린더 | 검토함 | AI입력 | 설정
- Stats(통계)는 Bottom Nav에서 제외, 홈 또는 별도 진입점에서 접근

## 3) 데이터 모델(Room) 초안
- RawEvent
  - id, createdAt, sourcePackage, title, text, postedAt, hash
- Candidate(ParsedTransactionCandidate)
  - id, rawEventId, type(INCOME/EXPENSE), amount, merchant, occurredAt, channel, status(CANDIDATE/CANCEL_CANDIDATE/IGNORED), confidence
- Transaction
  - id, type, amount, merchant, categoryId, occurredAt, note, source(AUTO/MANUAL), linkRawEventId?, state(NORMAL/CANCELED)
- Category
  - id, name, type, icon
- PointLedger
  - id, delta, reason, createdAt, refId?
- (추후 과금 대비, MVP 미사용) Purchase
  - id, productId, purchaseToken, orderId?, state(PURCHASED/CANCELED/REFUNDED), purchaseTime, acknowledged, consumed

## 4) 파싱 규칙(초안 원칙)
- 1차: 키워드로 결제/입출금/승인/취소 판단(카드사/은행/페이별 패턴 테이블)
- 2차: 금액 추출(원화 `(\d{1,3}(,\d{3})+|\d+)원`)
- 3차: 가맹점/기관명 추출(문구 패턴별)
- 4차: 승인/취소/입금/출금 분류
- 파서는 “플러그인 구조”로(카드사/은행별 RuleSet).
- 실패 시 Candidate 생성하지 않고 RawEvent만 저장(또는 Inbox에 ‘미분류’로 넣기 옵션).

## 5) 백그라운드/권한/정책
- NotificationListenerService 사용.
- 포그라운드 서비스는 되도록 피하고, 필요 시만 옵션으로.
- 개인정보 최소 수집(원문 저장 시 민감정보 최소화, 로컬 저장 기본).
- 설정 화면에 “무슨 데이터를 왜 수집하는지” 명확히 표기.

## 6) 개발 작업 순서(권장)
1) 프로젝트 스캐폴딩(Compose + Room + Navigation)
2) RawEvent 수집 저장(알림 리스너) + 중복 해시
3) 파싱 엔진 v0(금액/승인/취소/가맹점 기본)
4) Inbox UI + “확정”으로 Transaction 생성
5) 수동 입력/수정 + 카테고리
6) 포인트 원장 + 지급(확정/정산)
7) AI 자연어 입력 화면
8) 통계/리포트(간단)
9) 테스트/크래시 방지 + 성능 최적화
10) (추후) 포인트 충전(현질) + 구매 복원 + 구매 내역

## 7) 코딩 규칙
- Kotlin, Coroutines, Flow
- Repository 패턴 + UseCase(간단)
- UI 상태는 immutable data class + ViewModel
- Room은 Transactional update 사용(취소/매칭 시)
- 로그에는 민감정보(전체 알림 본문) 출력 금지

## 8) 보안/키 관리(중요)
- API 키(LLM/메일/기타)는 앱에 박지 말 것.
- 서버 연동 시: 앱 → NestJS → LLM(Claude/Groq/Gemini) 형태로만.
- (과거 노출된 키가 있다면) 즉시 폐기/재발급.

## 9) 명령(에이전트에게)
- 위 스펙을 기준으로 코드를 생성/수정하라.
- 파일 수정은 최소 diff로, 컴파일 깨지지 않게.
- 구현 후 “빌드/런” 가능한 상태를 유지하라.
- ⚠️ **결제/과금/구매 내역/영수증 검증/복원 기능은 MVP에서 구현하지 말 것.**
