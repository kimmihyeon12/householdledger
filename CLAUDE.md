# CLAUDE.md

## Git Commit Convention

### Commit Message Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Type

| Type | Description |
|------|-------------|
| `feat` | 새로운 기능 추가 |
| `fix` | 버그 수정 |
| `docs` | 문서 수정 (README, CLAUDE.md 등) |
| `style` | 코드 포맷팅, 세미콜론 누락 등 (코드 변경 없음) |
| `refactor` | 코드 리팩토링 (기능 변경 없음) |
| `perf` | 성능 개선 |
| `test` | 테스트 추가 또는 수정 |
| `chore` | 빌드, 설정 파일 수정 등 (소스 코드 변경 없음) |
| `ci` | CI/CD 설정 변경 |
| `revert` | 이전 커밋 되돌리기 |

### Scope

프로젝트 모듈을 명시한다. 생략 가능.

- `angular` - Angular 프론트엔드 프로젝트
- `nest` - NestJS 백엔드 프로젝트
- `app` - 앱 프로젝트
- `android` - Android 프로젝트

### Subject

- 한글 또는 영어로 간결하게 작성
- 마침표(.) 사용하지 않음
- 명령형으로 작성 (예: "추가", "수정", "삭제")

### 예시

```
feat(angular): 로그인 페이지 추가
fix(nest): 사용자 인증 토큰 만료 오류 수정
docs: CLAUDE.md 커밋 컨벤션 문서 작성
refactor(app): 공통 유틸 함수 분리
chore: package.json 의존성 업데이트
style(angular): 코드 포맷팅 정리
test(nest): 사용자 API 단위 테스트 추가
perf(android): 이미지 로딩 최적화
```

## Project Structure

```
project/
├── angular/          # Angular 프론트엔드
│   ├── all-in-one-admin
│   ├── all-in-one-web
│   ├── all-in-one-external-web
│   ├── chat-bot
│   ├── household-ledger
│   └── power-app
├── nest/             # NestJS 백엔드
│   └── customer-chatbot-backend
├── app/              # 앱
│   └── power-app
└── android/          # Android
    └── householdledger
```

## General Rules

- 커밋은 하나의 논리적 변경 단위로 만든다.
- 관련 없는 변경사항은 별도의 커밋으로 분리한다.
- 커밋 전 반드시 변경 내용을 확인한다.
- 민감 정보(.env, credentials 등)는 커밋하지 않는다.

## Commit Splitting Rules (커밋 분리 규칙)

여러 변경사항이 있을 때 아래 기준으로 커밋을 분리한다.

### 분리 기준

1. **Type이 다른 경우** - 반드시 분리
   - 기능 추가(`feat`)와 버그 수정(`fix`)은 별도 커밋
   - 리팩토링(`refactor`)과 기능 추가(`feat`)는 별도 커밋
   - 문서 수정(`docs`)과 코드 변경은 별도 커밋

2. **Scope이 다른 경우** - 반드시 분리
   - `angular` 변경과 `nest` 변경은 별도 커밋
   - `android` 변경과 `app` 변경은 별도 커밋

3. **독립적인 기능 단위** - 가능한 분리
   - 로그인 기능과 회원가입 기능은 별도 커밋
   - UI 변경과 비즈니스 로직 변경은 별도 커밋

### 분리 예시

```
# BAD: 하나의 커밋에 여러 변경사항
feat(android): 로그인 기능 추가 및 버그 수정, 문서 업데이트

# GOOD: 논리적 단위로 분리
feat(android): 로그인 페이지 UI 구현
feat(android): 로그인 API 연동
fix(android): 자동 로그인 시 토큰 만료 처리
docs: CLAUDE.md 로그인 관련 문서 추가
```

### 분리하지 않아도 되는 경우

- 하나의 기능을 위한 여러 파일 수정 (예: Model + Repository + ViewModel)
- 같은 버그를 수정하기 위한 여러 파일 변경
- 코드 변경에 따른 필수적인 테스트 수정

## Android App (콩돈 - householdledger)

### 주요 기능

| 기능 | 설명 | 주요 파일 |
|------|------|-----------|
| 홈 화면 | 월별 요약 카드 (수입/지출/잔액), 헤더에 예산 칩, AI 자연어 거래 입력, 최근 거래 목록 | `ui/home/HomeScreen.kt` |
| 월별 예산 관리 | SharedPreferences 기반 월별 예산 설정, 잔액 = 예산 - 지출, 요약 카드 헤더의 칩으로 설정/표시 | `data/budget/BudgetPreferences.kt` |
| 거래 추가/수정 | 수입/지출 타입, 금액, 가맹점, 카테고리, 메모 입력 | `ui/transaction/AddEditTransactionScreen.kt` |
| 캘린더 | 월별 캘린더 뷰, 일별 수입/지출 표시, 날짜 클릭 시 거래 상세 | `ui/calendar/CalendarScreen.kt` |
| 수신함 | 자동 수집된 거래 후보 검토 (승인/거절) | `ui/inbox/InboxScreen.kt` |
| 설정 | 다크모드/라이트모드/시스템 테마 설정 | `ui/settings/SettingsScreen.kt` |
| 스플래시 | 앱 시작 시 스플래시 화면 | `ui/splash/SplashScreen.kt` |

### 데이터 구조

- **MockData**: 임시 더미 데이터 (`data/mock/MockData.kt`)
- **BudgetPreferences**: SharedPreferences 기반 월별 예산 저장 (`data/budget/BudgetPreferences.kt`)
- **ThemePreferences**: SharedPreferences 기반 테마 모드 저장 (`ui/theme/ThemeMode.kt`)

### UI 규칙

- 모달/바텀시트 배경: `Color.White` 고정 (다크모드에서도 흰색)
- 거래 추가/수정 화면 배경: `Color.White` 고정
- 홈 요약 카드: 그라데이션 배경 (AccentGradientStart → AccentGradientEnd → Navy900)
- 예산 칩: 요약 카드 헤더 오른쪽에 `[+ 예산 300만]` 형태, 금액은 자동 단위 변환 (원/천/만/억)
- 예산 수정 다이얼로그: `Color.White` 고정, 빠른 금액 칩 (100만/200만/300만)
- 색상 테마: Navy + Violet 기반 (`ui/theme/Color.kt`)
