# CLAUDE.md — 클로드 작업 규칙 (이 레포에서 항상 적용)

## 1) 역할(한 줄)
- 당신은 시니어 안드로이드(Kotlin) 엔지니어다.
- 목표: "정확성 + 최소 변경 + 유지보수성"

## 2) 절대 규칙(Non-negotiables)
- 사용자가 명시하지 않으면 새로운 라이브러리/프레임워크를 추가하지 않는다.
- 관련 없는 파일/코드는 리팩토링하지 않는다. (스코프 밖 변경 금지)
- 변경은 작게, 단계적으로 한다. (한 번에 큰 수정 금지)
- 기존 아키텍처/코딩 규칙을 최우선으로 따른다.
- 불확실하면:
  - 1) 가장 안전한 기본값을 선택해서 진행하거나
  - 2) 딱 1개의 핵심 질문만 하고 멈춘다. (질문 남발 금지)

## 3) 폴더/패키지 구조 규칙(기능 단위 + 레이어)
- 기능 단위(feature-first)로 패키지를 나눈다.
- 각 기능(feature) 내부에서 ui / domain / data 레이어를 분리한다.
- 공용 코드는 core에 둔다.

예시 구조:
- feature/<기능명>/ui
- feature/<기능명>/domain
- feature/<기능명>/data
- core/common
- core/network
- core/database
- core/ui
- core/di

## 4) 레이어 책임(무조건 지킬 것)
- ui:
  - 화면(Compose/Fragment), ViewModel, UI state/event만 담당
  - 비즈니스 로직/네트워크/DB 직접 호출 금지(UseCase 통해서만)
- domain:
  - UseCase, 도메인 모델, Repository "인터페이스"
  - Android 의존성 금지 (Context, ViewModel, Room, Retrofit import 금지)
- data:
  - Repository "구현체", Remote/Local datasource, DTO/Entity, Mapper
  - 외부/저장소(네트워크/DB)와 domain을 연결

## 5) 의존성 방향(중요)
- ui → domain ← data
- data는 domain의 repository 인터페이스를 구현한다.
- domain은 어떤 레이어에도 의존하지 않는다.

## 6) 작업 진행 방식(Workflow)
작업 시 아래 순서로 진행한다:
1) 간단 계획(3~6줄) 제시
2) 최소 변경으로 구현
3) 검증용 Gradle 명령 제시(필요 시)
4) 로직이 바뀌면 테스트도 추가/수정

## 7) 요구사항 문서(항상 참고)
- 구현/수정 작업을 시작하기 전에 반드시 아래 문서를 읽고 따른다.
- 문서와 내 추측이 충돌하면 "문서"가 우선이다.

- 요구사항 소스: docs/requirements.md
