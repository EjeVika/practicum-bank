import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name("Create account")
    description "Создать нового пользователя и счет"
    request {
        method POST()
        url $(consumer('/api/v1/accounts'), producer('/accounts'))
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
                "login": "ivanov",
                "name": "Иван Иванов"
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
                "birthday": null,
                "balance": 0.00
           }
           '''
    }
}