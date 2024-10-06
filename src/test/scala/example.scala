
import org.scalatest.flatspec.AnyFlatSpec


//In a AnyFlatSpec, you name the subject once, with a behavior
// of clause or its shorthand, then write tests for that subject with it should/must/can "do something" phrases.
//Because sometimes the subject could be plural, you can alternatively use they instead of it:

// change it to ignore to ignore the test
class SetSpec extends AnyFlatSpec {

  "An empty Set" should "have size 0" in {
    assert(Set.empty.size === 0)
  }

  //Each it refers to the most recently declared subject. In this case "An empty Set"
  it should "produce NoSuchElementException when head is invoked" in {
    assertThrows[NoSuchElementException] {
      Set.empty.head
    }
  }
}
