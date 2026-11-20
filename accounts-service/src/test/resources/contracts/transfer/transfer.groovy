import org.springframework.cloud.contract.spec.Contract

[
        Contract.make {
            name("Transfer")
            description "Перевести деньги с одного счета на другой"
            request {
                method POST()
                url '/accounts/transfer'
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
                        "toAccountLogin": "sidorov",
                        "fromAccountLogin": "ivanov"
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
                        "sum":100.5,
                        "toAccountLogin":"sidorov",
                        "toAccountBalance":200.5,
                        "fromAccountLogin":"ivanov",
                        "fromAccountBalance":100,
                        "message":"Переведено 100.5 рублей на счет sidorov"
                    }
                    '''
            }
        },
        Contract.make {
            name("Transfer to unknown account fails")
            description "Не удалось перевести деньги на счет (со счета) неизвестного пользователя"
            request {
                method POST()
                url '/accounts/transfer'
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
                        "toAccountLogin": "petrov",
                        "fromAccountLogin": "ivanov"
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
                        "instance":"/accounts/transfer",
                        "status":404,
                        "title":"Не найден счет для пользователя: petrov"
                    }
                    '''
            }
        },
        Contract.make {
            name("Transfer from account fails due to low balance")
            description "Не удалось перевести деньги так как на счету их недостаточно"
            request {
                method POST()
                url '/accounts/transfer'
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
                        "sum": "10000",
                        "toAccountLogin": "sidorov",
                        "fromAccountLogin": "ivanov"
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
                        "detail":"Недостаточно средств на счете у пользователя: ivanov",
                        "instance":"/accounts/transfer",
                        "status":409,
                        "title":"Недостаточно средств на счете у пользователя: ivanov"
                    }
                    '''
            }
        }
]