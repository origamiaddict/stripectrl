package ch.origamiaddict.stripecontrol.artnet.internal;

public class ArtNetDmxPacket {

  private int sequence;
  private int physical;
  private int universe;
  private int length;
  private int[] data;
  
  /** Creates a new instance of ArtDmx */
  public ArtNetDmxPacket()
  {
  }
  
  public void setSequence(int seq)
  {
    sequence = seq;
  }
  
  public void setPhysical(int phy)
  {
    physical = phy;
  }
  
  public void setUniverse(int uni)
  {
    universe = uni;
  }
  
  public void setLength(int len)
  {
    length = len;
  }
  
  public void setData(int[] dat)
  {
    data = dat;
  }
  
  public int getSequence()
  {
    return sequence;
  }
  
  public int getPhysical()
  {
    return physical;
  }
  
  public int getUniverse()
  {
    return universe;
  }
  
  public int getLength()
  {
    return length;
  }
  
  public int[] getData()
  {
    return data;
  }  
}
