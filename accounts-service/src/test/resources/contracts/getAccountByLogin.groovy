import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Get account by login"
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
}