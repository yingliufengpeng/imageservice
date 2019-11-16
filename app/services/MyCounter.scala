package services

import java.util.concurrent.atomic.AtomicInteger

import javax.inject.Singleton

trait MyCounter {
  def nextCount(): Int
}


@Singleton
class MyAtomicCounter extends MyCounter {
  private val atomicCounter = new AtomicInteger()
  override def nextCount(): Int = atomicCounter.getAndIncrement()
}
