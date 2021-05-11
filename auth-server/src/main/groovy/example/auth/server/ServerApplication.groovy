package example.auth.server

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "15s")
@SpringBootApplication
class ServerApplication {

	static void main(String[] args) {
		SpringApplication.run(ServerApplication, args)
	}

}
