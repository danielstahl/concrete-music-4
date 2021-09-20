package net.soundmining

import net.soundmining.synth._
import net.soundmining.modular.ModularSynth._
import net.soundmining.synth.SoundPlay
import net.soundmining.synth.SuperColliderClient.{allocRead, loadDir}


object ConcreteMusic4 {

    val CLOCK_SPRING_SPECTRUM_FREQS = Seq(206.504, 160.774, 380.784, 448.902, 1051.13, 1267.64, 1520.84, 1630.4)

    implicit val client: SuperColliderClient = SuperColliderClient()
    //val SOUND_BASE_DIR = "/Users/danielstahl/Documents/Projects/musique-concrete-iii/sounds/"
    val SOUND_BASE_DIR = "/Users/danielstahl/Documents/Music/sounds/"
    val SYNTH_DIR = "/Users/danielstahl/Documents/Projects/soundmining-modular/src/main/sc/synths"

    def init(): Unit = {
        println("Starting up SuperCollider client")
        client.start
        Instrument.setupNodes(client)
        client.send(loadDir(SYNTH_DIR))
        client.send(allocRead(2, s"${SOUND_BASE_DIR}clock-spring-1.flac"))

        // Look at the synth def here https://github.com/supercollider/supercollider/blob/3.11/SCClassLibrary/Common/GUI/tools/ServerMeter.sc
        // https://www.scala-lang.org/api/current/scala/Console$.html
    }

    def stop(): Unit = {
        println("Stopping Supercollider client")
        client.stop
    }

    val sounds = Map(
        "clock-spring-1" -> SoundPlay(2, 2.613, 4.200, highPass = Some(1760), lowPass = Some(1625), 
            amp = volume => relativeThreeBlockcontrol(0, 0.3, volume * 2, volume * 2, 0.2, 0, Right(Instrument.LINEAR)),
            spectrumFreqs = CLOCK_SPRING_SPECTRUM_FREQS),
        "clock-spring-2" -> SoundPlay(2, 0.068, 1.079, highPass = Some(1760), lowPass = Some(1625), 
            amp = volume => relativeThreeBlockcontrol(0, 0.01, volume * 2, volume * 2, 0.3, 0, Right(Instrument.LINEAR)),
            spectrumFreqs = CLOCK_SPRING_SPECTRUM_FREQS),
        "clock-spring-3" -> SoundPlay(2, 1.077, 2.445, highPass = Some(1760), lowPass = Some(1625), 
            amp = volume => relativeThreeBlockcontrol(0, 0.01, volume * 2, volume * 2, 0.15, 0, Right(Instrument.LINEAR)),
            spectrumFreqs = CLOCK_SPRING_SPECTRUM_FREQS)
    )

    val soundPlays = SoundPlays(sounds, numberOfOutputBuses = 16)

    import soundPlays._

    def playClockSpring(start: Float = 0): Unit = {
        client.resetClock
        playSound("clock-spring-1", 0 + start)
        //playSoundHighpass("clock-spring-1", 3 + start)
        //playSoundLowpass("clock-spring-1", 6 + start)

        playSound("clock-spring-2", 9 + start)
        //playSoundHighpass("clock-spring-2", 12 + start)
        //playSoundLowpass("clock-spring-2", 15 + start)

        playSound("clock-spring-3", 18 + start)
        //playSoundHighpass("clock-spring-3", 21 + start)
        //playSoundLowpass("clock-spring-3", 24 + start)
    }

    def theme1(start: Double = 0): Unit = {
        client.resetClock
        playSound("clock-spring-2", 0, pan = -0.5f, highPass = soundPlays("clock-spring-2").highPass)
        playSound("clock-spring-1", 0, pan = 0.5f, lowPass = soundPlays("clock-spring-1").lowPass)
        // part of a development
        playSound("clock-spring-1", 0.6, pan = 0.2f, rate = 1.05, highPass = soundPlays("clock-spring-1").highPass)
    }

    def theme2(start: Double = 0, m1: Option[Double] = None, m2: Option[Double] = None): Unit = {
        client.resetClock

        val modFreq1 = m1.orElse(soundPlays("clock-spring-1").spectrumFreqs.lift(6))
        val modFreq2 = m2.orElse(soundPlays("clock-spring-1").spectrumFreqs.lift(7))

        playSound("clock-spring-1", 0, volume = 2f, rate = 0.5, pan = -0.5f, ringModulate = modFreq1)
        playSound("clock-spring-1", 0.1, volume = 2f, rate = 0.5, pan = 0.5f, ringModulate = modFreq2)
    }

    
    // List(206.504, 160.774, 380.784, 448.902, 1051.13, 1267.64, 1520.84, 1630.4)
    // r1 = Some(160.774 / 206.504), r2 = Some(206.504 / 380.784)
    // r1 = Some(206.504 /160.774), r2 = Some(380.784 / 206.504)
    def theme3(start: Double = 0, r1: Option[Double] = None, r2: Option[Double] = None): Unit = {
        client.resetClock

        val rate1 = r1.getOrElse(1.0)
        val rate2 = r2.getOrElse(CLOCK_SPRING_SPECTRUM_FREQS(1) / CLOCK_SPRING_SPECTRUM_FREQS(0)) // 0.7785515050555922

        playSound("clock-spring-1", 0, volume = 2f, rate = rate1, pan = -0.5f)
        playSound("clock-spring-1", 1, volume = 2f, rate = rate2, pan = 0.5f)
    }

    def theme4(): Unit = {
        client.resetClock

        val rates = Seq((1, 1), (1, 0), (1, 2), (1, 3), (1, 4), (1, 5))
            .map {
                case (i, j) => CLOCK_SPRING_SPECTRUM_FREQS(i) / CLOCK_SPRING_SPECTRUM_FREQS(j)
            }
        println(s"rates $rates")

         playSound("clock-spring-1", 0, volume = 2f, rate = rates(0), pan = 0f)   
         playSound("clock-spring-1", 3, volume = 2f, rate = rates(1), pan = 0f)

         playSound("clock-spring-1", 10, volume = 2f, rate = rates(2), pan = 0f)    
         playSound("clock-spring-1", 15, volume = 2f, rate = rates(3), pan = 0f)    

         playSound("clock-spring-1", 20, volume = 2f, rate = rates(4), pan = 0f)    
         playSound("clock-spring-1", 25, volume = 2f, rate = rates(5), pan = 0f)    
    }

    def theme5(reset: Boolean = true, start: Double = 0): Unit = {
        if(reset) client.resetClock

        val rates = Seq((1, 1), (1, 0))
            .map {
                case (i, j) => CLOCK_SPRING_SPECTRUM_FREQS(i) / CLOCK_SPRING_SPECTRUM_FREQS(j)
            }
        println(s"rates $rates")

        val durations = rates.map(rate => soundPlays("clock-spring-1").duration(rate))
        println(s"duration $durations")

        val times = Melody.absolute(start, Seq(
            durations(0) * 3, durations(0) * 2, durations(0) * 3, 
            durations(1) * 2, durations(1) * 1, durations(1) * 2,
            durations(0) * 3, durations(0) * 2, durations(0) * 3, 
            durations(1) * 2, durations(1) * 1, durations(1) * 2,
            durations(0) * 3, durations(0) * 2, durations(0) * 3))

        println(s"times $times")

        val pans = Seq(
            0.6, -0.3, -0.7, 0.3, 0.8,
            0.6, -0.3, -0.7, 0.3, 0.8,
            0.6, -0.3, -0.7, 0.3, 0.8,
            0.6, -0.3, -0.7, 0.3, 0.8
        )   

        val rings = Seq(4, 6, 7, 5, 6, 4, 6, 7, 5, 6, 4, 6, 7, 5, 6, 4, 6, 7, 5, 6)
            .map(i => Some(CLOCK_SPRING_SPECTRUM_FREQS(i)))

        // Seq(206.504, 160.774, 380.784, 448.902, 1051.13, 1267.64, 1520.84, 1630.4)
        
        playSound("clock-spring-1", times(0), volume = 1f, rate = rates(0), pan = pans(0), outputBus = 0)
        playSound("clock-spring-1", times(0) + 0.05, volume = 2f, rate = rates(0), pan = pans(0) * -1, ringModulate = rings(0), outputBus = 2)
        playSound("clock-spring-2", times(0), volume = 0.7f, rate = rates(0), pan = pans(0), highPass = soundPlays("clock-spring-2").highPass, outputBus = 4)

        playSound("clock-spring-1", times(1), volume = 1f, rate = rates(1), pan = pans(1), highPass = rings(1), outputBus = 0)    
        playSound("clock-spring-1", times(1) + 0.05, volume = 2f, rate = rates(1), pan = pans(1) * -1, ringModulate = rings(1), outputBus = 2) 

        playSound("clock-spring-1", times(2), volume = 1f, rate = rates(0), pan = pans(2), outputBus = 0) 
        playSound("clock-spring-1", times(2) + 0.05, volume = 2f, rate = rates(0), pan = pans(2) * -1, ringModulate = rings(2), outputBus = 2) 

        playSound("clock-spring-1", times(3), volume = 1f, rate = rates(1), pan = pans(3), outputBus = 0)    
        playSound("clock-spring-1", times(3) + 0.05, volume = 2f, rate = rates(1), pan = pans(3) * -1, ringModulate = rings(3), outputBus = 2)    

        playSound("clock-spring-1", times(4), volume = 1f, rate = rates(0), pan = pans(4), outputBus = 0)  
        playSound("clock-spring-1", times(4) + 0.05, volume = 2f, rate = rates(0), pan = pans(4) * -1, ringModulate = rings(4), outputBus = 2)

        playSound("clock-spring-1", times(5), volume = 1f, rate = rates(1), pan = pans(5), outputBus = 0)
        playSound("clock-spring-1", times(5) + 0.05, volume = 2f, rate = rates(1), pan = pans(5) * -1, ringModulate = rings(5), outputBus = 2)    
        playSound("clock-spring-2", times(5), volume = 0.7f, rate = rates(1), pan = pans(5), highPass = soundPlays("clock-spring-2").highPass, outputBus = 4)

        playSound("clock-spring-1", times(6), volume = 1f, rate = rates(0), pan = pans(6), outputBus = 0)
        playSound("clock-spring-1", times(6) + 0.05, volume = 2f, rate = rates(0), pan = pans(6) * -1, ringModulate = rings(6), outputBus = 2)

        playSound("clock-spring-1", times(7), volume = 1f, rate = rates(1), pan = pans(7), outputBus = 0)
        playSound("clock-spring-1", times(7) + 0.05, volume = 2f, rate = rates(1), pan = pans(7) * -1, ringModulate = rings(7), outputBus = 2)    

        playSound("clock-spring-1", times(8), volume = 1f, rate = rates(0), pan = pans(8), outputBus = 0)
        playSound("clock-spring-1", times(8) + 0.05, volume = 2f, rate = rates(0), pan = pans(8) * -1, ringModulate = rings(8), outputBus = 2)

        playSound("clock-spring-1", times(9), volume = 1f, rate = rates(1), pan = pans(9), outputBus = 0)
        playSound("clock-spring-1", times(9) + 0.05, volume = 2f, rate = rates(1), pan = pans(9) * -1, ringModulate = rings(9), outputBus = 2)    

        playSound("clock-spring-1", times(10), volume = 1f, rate = rates(0), pan = pans(10), outputBus = 0) 
        playSound("clock-spring-1", times(10) + 0.05, volume = 2f, rate = rates(0), pan = pans(10) * -1, ringModulate = rings(10), outputBus = 2) 
        playSound("clock-spring-2", times(10), volume = 0.7f, rate = rates(0), pan = pans(10), highPass = soundPlays("clock-spring-2").highPass, outputBus = 4) 

        playSound("clock-spring-1", times(11), volume = 1f, rate = rates(1), pan = pans(11), outputBus = 0)    
        playSound("clock-spring-1", times(11) + 0.05, volume = 2f, rate = rates(1), pan = pans(11) * -1, ringModulate = rings(11), outputBus = 2) 

        playSound("clock-spring-1", times(12), volume = 1f, rate = rates(0), pan = pans(12), outputBus = 0)    
        playSound("clock-spring-1", times(12) + 0.05, volume = 2f, rate = rates(0), pan = pans(12) * -1, ringModulate = rings(12), outputBus = 2) 
    }

    def theme6(reset: Boolean = true, start: Double = 0): Unit = {
        if(reset) client.resetClock

        val rates = Seq((1, 2), (1, 3))
            .map {
                case (i, j) => CLOCK_SPRING_SPECTRUM_FREQS(i) / CLOCK_SPRING_SPECTRUM_FREQS(j)
            }
        println(s"rates $rates")
        
        val durations = rates.map(rate => soundPlays("clock-spring-1").duration(rate))
        println(s"duration $durations")

        val times = Melody.absolute(start, Seq(
            durations(0) * 1, durations(0) * 2, durations(1) * 1, durations(0) * 2, durations(0) * 1, durations(1) * 1))

        println(s"times $times")

        val rings = Seq(4, 6, 7, 5, 6, 4, 6, 7, 5, 6, 4, 6, 7, 5, 6, 4, 6, 7, 5, 6)
            .map(i => Some(CLOCK_SPRING_SPECTRUM_FREQS(i)))

        val pans = Seq(
            0.6, -0.3, -0.7, 0.3, 0.8,
            0.6, -0.3, -0.7, 0.3, 0.8,
            0.6, -0.3, -0.7, 0.3, 0.8,
            0.6, -0.3, -0.7, 0.3, 0.8
        )   

        playSound("clock-spring-1", times(0), volume = 1f, rate = rates(0), pan = pans(0), outputBus = 0)   
        playSound("clock-spring-1", times(0) + 0.05, volume = 2f, rate = rates(0), pan = pans(0) * -1, ringModulate = rings(0), outputBus = 2)

        playSound("clock-spring-1", times(1), volume = 1f, rate = rates(1), pan = pans(1), outputBus = 0)    
        playSound("clock-spring-1", times(1) + 0.05, volume = 2f, rate = rates(1), pan = pans(1) * -1, ringModulate = rings(1), outputBus = 2)

        playSound("clock-spring-1", times(2), volume = 1f, rate = rates(0), pan = pans(2), outputBus = 0) 
        playSound("clock-spring-1", times(2) + 0.05, volume = 2f, rate = rates(0), pan = pans(2) * -1, ringModulate = rings(2), outputBus = 2) 

        playSound("clock-spring-1", times(3), volume = 1f, rate = rates(1), pan = pans(3), outputBus = 0)    
        playSound("clock-spring-1", times(3) + 0.05, volume = 2f, rate = rates(1), pan = pans(3) * -1, ringModulate = rings(3), outputBus = 2)    
        playSound("clock-spring-2", times(3), volume = 0.7f, rate = rates(1), pan = pans(3), highPass = soundPlays("clock-spring-2").highPass, outputBus = 4) 

        playSound("clock-spring-1", times(4), volume = 1f, rate = rates(0), pan = pans(4), outputBus = 0)  
        playSound("clock-spring-1", times(4) + 0.05, volume = 2f, rate = rates(0), pan = pans(4) * -1, ringModulate = rings(4), outputBus = 2)

        playSound("clock-spring-1", times(5), volume = 1f, rate = rates(1), pan = pans(5), outputBus = 0)  
        playSound("clock-spring-1", times(5) + 0.05, volume = 2f, rate = rates(1), pan = pans(5) * -1, ringModulate = rings(5), outputBus = 2)
    }

    def theme7(reset: Boolean = true, start: Double = 0): Unit = {
        if(reset) client.resetClock

        val rates = Seq((1, 4), (1, 5))
            .map {
                case (i, j) => CLOCK_SPRING_SPECTRUM_FREQS(i) / CLOCK_SPRING_SPECTRUM_FREQS(j)
            }
        println(s"rates $rates")

        val durations = rates.map(rate => soundPlays("clock-spring-1").duration(rate))
        println(s"duration $durations")

        val times = Melody.absolute(start, Seq(
            durations(0) * 1, durations(1) * 1))
        println(s"times $times")

        playSound("clock-spring-1", times(0), volume = 1f, rate = rates(0), pan = 0.5f, outputBus = 6)    
        playSound("clock-spring-1", times(0) + 0.05, volume = 2f, rate = rates(0), pan = 0.5 * -1, ringModulate = Some(CLOCK_SPRING_SPECTRUM_FREQS(4)), outputBus = 8)

        playSound("clock-spring-1", times(1), volume = 1f, rate = rates(1), pan = 0.3f, outputBus = 6)    
        playSound("clock-spring-1", times(1) + 0.05, volume = 2f, rate = rates(1), pan = 0.3 * -1, ringModulate = Some(CLOCK_SPRING_SPECTRUM_FREQS(6)), outputBus = 8)
    }

    def play1v1(s2: Option[Double] = None): Unit = {
        client.resetClock

        theme5(reset = false, start = 0)
        theme6(reset = false, start = 53.711)

        theme7(reset = false, start = 22.88)
    }
}
