# account-manager

```
.
├── main
│   ├── kotlin
│   │   └── com
│   │       └── example
│   │           ├── Application.kt
│   │           ├── Routing.kt
│   │           ├── dtos
│   │           │   └── CreateTransferDTO.kt
│   │           ├── entities
│   │           │   ├── Account.kt
│   │           │   └── Transfer.kt
│   │           ├── exceptions
│   │           │   ├── InsufficentBalanceException.kt
│   │           │   ├── InvalidCurrencyException.kt
│   │           │   └── NotFoundException.kt
│   │           ├── repositories
│   │           │   ├── AccountRepository.kt
│   │           │   └── TransferRepository.kt
│   │           ├── serializers
│   │           │   └── BigDecimalSerializer.kt
│   │           └── services
│   │               ├── AccountService.kt
│   │               └── TransferService.kt
│   └── resources
│       ├── application.conf
│       └── logback.xml
└── test
    ├── kotlin
    │   └── ApplicationTest.kt
    └── resources

14 directories, 16 files
```

## APIs

```
GET /v1/accounts/:id

GET /v1/transfers
POST /v1/transfers
```

## Further improvement

Currently, it is happy flow only

- Add CreateTransferDTO field validation
- Error handling
- Add transfer for fund flow records
