// newio/AvailableCharSets.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Displays Charsets and aliases
import java.nio.charset.*;
import java.util.*;

public class AvailableCharSets {
  public static void main(String[] args) {
    SortedMap<String,Charset> charSets =
      Charset.availableCharsets();
    for(String csName : charSets.keySet()) {
      System.out.print(csName);
      Iterator aliases = charSets.get(csName)
        .aliases().iterator();
      if(aliases.hasNext())
        System.out.print(": ");
      while(aliases.hasNext()) {
        System.out.print(aliases.next());
        if(aliases.hasNext())
          System.out.print(", ");
      }
      System.out.println();
    }
  }
}
/* Output: (First 7 Lines)
Big5: csBig5
Big5-HKSCS: big5-hkscs, big5hk, Big5_HKSCS, big5hkscs
CESU-8: CESU8, csCESU-8
EUC-JP: csEUCPkdFmtjapanese, x-euc-jp, eucjis,
Extended_UNIX_Code_Packed_Format_for_Japanese, euc_jp,
eucjp, x-eucjp
EUC-KR: ksc5601-1987, csEUCKR, ksc5601_1987, ksc5601,
5601,
euc_kr, ksc_5601, ks_c_5601-1987, euckr
GB18030: gb18030-2000
GB2312: gb2312, euc-cn, x-EUC-CN, euccn, EUC_CN,
gb2312-80,
gb2312-1980
                  ...
*/
