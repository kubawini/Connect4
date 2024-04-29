import korlibs.korge.gradle.*

plugins {
	alias(libs.plugins.korge)
}

korge {
	id = "com.sample.demo"
	targetJvm()
	targetJs()
	targetAndroid()

	serializationJson()
}


dependencies {
    add("commonMainApi", project(":deps"))
    //add("commonMainApi", project(":korge-dragonbones"))
}

