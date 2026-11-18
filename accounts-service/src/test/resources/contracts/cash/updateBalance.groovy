import org.springframework.cloud.contract.spec.Contract

[
        Contract.make {
            name("Deposit cash to account")
            description "Положить деньги на счет"
            request {
                method POST()
                url '/accounts/cash/ivanov'
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
                "sum": "100.5",
                "action": "PUT"
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
                "login":"ivanov",
                "resultBalance":200.5,
                "sum":100.5,
                "action":"PUT",
                "message":"На счет успешно положено 100.5 рублей"
            }
            '''
            }
        },
        Contract.make {
            name("Deposit cash to unknown account fails")
            description "Не удалось положить деньги на счет неизвестного пользователя"
            request {
                method POST()
                url '/accounts/cash/petrov'
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
                        "sum": "100.5",
                        "action": "PUT"
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
                        "instance":"/accounts/cash/petrov",
                        "status":404,
                        "title":"Не найден счет для пользователя: petrov"
                    }
                    '''
            }
        },
        Contract.make {
            name("Withdraw cash from account success")
            description "Успешно снять деньги со счета"
            request {
                method POST()
                url '/accounts/cash/sidorov'
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
                        "sum": "100.5",
                        "action": "GET"
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
                        "login":"sidorov",
                        "resultBalance":200,
                        "sum":100.5,
                        "action":"GET",
                        "message":"На счет успешно положено 100.5 рублей"
                    }
                    '''
            }
        },
        Contract.make {
            name("Withdraw cash from account fails")
            description "Не удалось снять деньги так как на счету их недостаточно"
            request {
                method POST()
                url '/accounts/cash/fedorov'
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
                        "sum": "100.5",
                        "action": "GET"
                   }
                   '''
            }
            response {
                status CONFLICT()
                headers {
                    header 'Content-Type', 'application/problem+json'
                }
                body '''
                    {
                        "detail":"Недостаточно средств на счете у пользователя: fedorov",
                        "instance":"/accounts/cash/fedorov",
                        "status":409,
                        "title":"Недостаточно средств на счете у пользователя: fedorov"
                    }
                    '''
            }
        },
        Contract.make {
            name("Unknown action type")
            description "В теле запроса передана операция неизвестного типа"
            request {
                method POST()
                url '/accounts/cash/fedorov'
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
                        "sum": "100.5",
                        "action": "UNKNOWN"
                   }
                   '''
            }
            response {
                status BAD_REQUEST()
            }
        }
]