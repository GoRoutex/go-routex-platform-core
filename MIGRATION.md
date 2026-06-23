# Modular Monolith Migration Status

## Current State

`go-routex-platform-core` is now a Maven multi-module modular monolith scaffold with the five Core Operations & Transaction Engine services migrated as compileable modules.

## Modules

- `platform-core-app`: Spring Boot entrypoint.
- `platform-core-common`: shared cross-cutting code and protobuf contracts.
- `platform-merchant`: migrated from `go-routex-merchant-platform`.
- `platform-booking`: migrated from `go-routex-booking-service`.
- `platform-driver`: migrated from `go-routex-driver-service`.
- `platform-management`: migrated from `go-routex-management-service`.
- `platform-payment`: migrated from `go-routex-payment-service`.

## Preserved External REST Prefixes

- Merchant: `/api/v1/merchant-service`.
- Booking: `/api/v1/booking-service`.
- Driver: `/api/v1/driver-service`.
- Management: `/api/v1/management`.
- Payment: `/api/v1/payment-service`.

## Compile Verification

The current reactor passed:

```bash
mvn clean compile
```

Maven compiled the full reactor successfully, including protobuf generation and all five migrated modules.

## Transitional Decisions

- Existing Java package names are preserved for the first migration phase to reduce risk.
- `platform-core-app` scans both the new platform package and the existing service packages.
- Existing REST controllers, DTOs, and endpoint constants are preserved.
- Existing internal Feign/gRPC clients are kept temporarily for compile compatibility. They should be replaced by local application ports after runtime startup is stabilized.
- The shared `booking_service.proto` uses the fuller booking-service contract because payment-service had an older subset that omitted `HoldSeat` messages.
- `go-routex-user-service` stays outside platform core. Do not migrate user-service-owned `UserEntity`, customer profile ownership, authentication, or user role ownership into this repository.
- Core modules must call user-service through gRPC for user/customer lookup workflows. The user-service boundary contract is `platform-core-common/src/main/proto/user_admin_service.proto`.
- Runtime config uses `grpc.client.userService.address`, backed by `USER_SERVICE_GRPC_ADDRESS`.

## Next Runtime Hardening Steps

1. Create a single `platform-core-app/src/main/resources/application.yml` by merging service configuration blocks.
2. Consolidate duplicate configs: security, OpenAPI, Redis, Redisson, Kafka, JPA auditing, exception handlers.
3. Resolve duplicate bean names before starting the combined Spring Boot app.
4. Replace internal calls inside the five core modules with local ports.
5. Keep gRPC server implementations only where services outside this core still call them.
6. Add endpoint contract tests for every preserved REST prefix.
7. Run Spring Boot startup verification from `platform-core-app`.
8. Move packages from legacy roots into `vn.com.routex.platform.*` only after endpoint and runtime compatibility are locked.

## Required Database Changes

Trip assignment now records explicit success/failure diagnostics for the async assign-to-sale flow:

```sql
alter table trip_assignment
    add column if not exists success_code varchar(64),
    add column if not exists success_description text,
    add column if not exists fail_code varchar(64),
    add column if not exists fail_description text;
```
