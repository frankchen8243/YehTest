import java.io.*;
public class gz2nc{

	public static void main(String[] args) {
		String path = "./";

		File file = new File(path);
		File[] array = file.listFiles();
		for(int i = 0;i < array.length;i++) {
//			System.out.println(array[i].toString());
			try {
				File person = array[i];
				String _fileName = person.getName();
				StringBuffer ic = new StringBuffer();
				ic.append(_fileName);
				String newName = (ic.toString()).substring(0, 56)+".nc";
				System.out.println("�W�r�O:"+newName);
				if (person.renameTo(new File(newName))){
					System.out.println("�ק令�\!");
				}
				else{
					System.out.println("�ק異��");
				}
			}
			catch(Throwable e){
				e.printStackTrace();
				System.out.println(e);
			}
		}//for end
	}
}
