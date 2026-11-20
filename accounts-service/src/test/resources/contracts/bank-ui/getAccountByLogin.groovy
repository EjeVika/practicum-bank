import org.springframework.cloud.contract.spec.Contract
[
        Contract.make {
            name("Get account by login success")
            description "Успешно найти пользователя по логину"
            request {
                method GET()
                url $(consumer('/api/v1/accounts/ivanov'), producer('/accounts/ivanov'))
                headers {
                    header 'Authorization', value(
                            // Для консьюмера (WireMock): любой Bearer-токен
                            consumer(regex('Bearer\\s+.+')),
                            // Для провайдера (MockMvc-тест): ровно этот токен
                            producer('Bearer test-token')
                    )
                }
            }
            response {
                status OK()
                headers {
                    contentType(applicationJson())
                }
                body '''
                    {
                        "login": "ivanov",
                        "name": "Иван Иванов",
                        "birthday": "1990-01-15",
                        "balance": 1500.50
                    }
                    '''
            }
        },
        Contract.make {
            name("Get account by login fail")
            description "Найти пользователя по логину не получилось"
            request {
                method GET()
                url $(consumer('/api/v1/accounts/petrov'), producer('/accounts/petrov'))
                headers {
                    header 'Authorization', value(
                            // Для консьюмера (WireMock): любой Bearer-токен
                            consumer(regex('Bearer\\s+.+')),
                            // Для провайдера (MockMvc-тест): ровно этот токен
                            producer('Bearer test-token')
                    )
                }
            }
            response {
                status NOT_FOUND()
                headers {
                    header 'Content-Type', 'application/problem+json'
                }
                body '''
                    {
                        "detail":"Не найден счет для пользователя: petrov",
                        "instance":"/accounts/petrov",
                        "status":404,
                        "title":"Не найден счет для пользователя: petrov"
                    }
                    '''
            }
        }
        ]