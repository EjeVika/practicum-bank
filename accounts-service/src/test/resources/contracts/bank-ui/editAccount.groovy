import org.springframework.cloud.contract.spec.Contract

[
        Contract.make {
            name("Edit account")
            description "Изменить имя и дату рождения пользователя"
            request {
                method PATCH()
                url $(consumer('/api/v1/accounts/ivanov'), producer('/accounts/ivanov'))
                headers {
                    contentType(applicationJson())
                    header 'Authorization', value(
                            // Для консьюмера (WireMock): любой Bearer-токен
                            consumer(regex('Bearer\\s+.+')),
                            // Для провайдера (MockMvc-тест): ровно этот токен
                            producer('Bearer test-token')
                    )
                }
                body '''
                    {
                        "name": "Иван Иванов",
                        "birthday": "1990-01-15"
                    }
                    '''
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
                        "balance": 100.00
                   }
                   '''
            }
        },
        Contract.make {
            name("Fail to edit account")
            description "Не удалось изменить имя и дату рождения пользователя"
            request {
                method PATCH()
                url $(consumer('/api/v1/accounts/petrov'), producer('/accounts/petrov'))
                headers {
                    contentType(applicationJson())
                    header 'Authorization', value(
                            // Для консьюмера (WireMock): любой Bearer-токен
                            consumer(regex('Bearer\\s+.+')),
                            // Для провайдера (MockMvc-тест): ровно этот токен
                            producer('Bearer test-token')
                    )
                }
                body '''
                    {
                        "name": "Иван Иванов",
                        "birthday": "1990-01-15"
                   }
                   '''
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