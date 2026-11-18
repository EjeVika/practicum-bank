import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name("Get accounts info short")
    description "Вывести краткую информацию о пользователях системы"
    request {
        method GET()
        url $(consumer('/api/v1/accounts'), producer('/accounts'))
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
            [
                {
                    "login": "ivanov",
                    "name": "Иван Иванов"
                },
                {
                    "login": "petrov",
                    "name": "Петр Петров"
                }
            ]
            '''
    }
}