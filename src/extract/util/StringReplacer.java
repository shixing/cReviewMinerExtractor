/**
 * 
 */
package extract.util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author shixing
 *
 */
public class StringReplacer {
	//����3������ĳ���Ҫ��ͬ
	//��Ҫƥ���ԭʼstring��Ŀǰ��֧��ͨ���
	private String[] source=null;
	//�滻���string
	private String[] dest=null;
	//����滻���ٴΣ�-1 �������
	private int[] times=null;
	private PreFixTree preTree=new PreFixTree();
	public StringReplacer(String[] source,String[] dest,int[] times)
	{
		this.source=source;
		this.dest=dest;
		this.times=times;
		this.compile();
	}
	
	private void compile()
	{
		PNode father[]=new PNode[this.source.length];
		for (int i=0;i<father.length;i++)
		{
			father[i]=this.preTree.root;
		}
		
		int index=0;
		while(true)
		{
			boolean touchEnd=true;
			for (int i=0;i<source.length;i++)
			{
				String pattern=source[i];
				if (pattern.length()<=index)
					continue;
				else if (pattern.length()-1==index)
				{
					//����replaceWord
					String c=pattern.charAt(index)+"";
					PNode newFather=father[i].addChild(c,i);
					father[i]=newFather;
				}
				else
				{
					String c=pattern.charAt(index)+"";
					PNode newFather=father[i].addChild(c, -1);
					father[i]=newFather;
					touchEnd=false;
				}
			}
			if (touchEnd)
				break;
			index++;
		}
	}
	
	public String replace(String string)
	{
		StringBuffer str=new StringBuffer(string);
		ArrayList<Traveller> travellers=new ArrayList<Traveller>();
		int invailPattern=0;
		for (int i=0;i<str.length();i++)
		{
			String c=str.charAt(i)+"";
			Traveller origin=new Traveller(this.preTree.root,i);
			travellers.add(origin);	//���ܳ�Ϊ��ǰ����ַ�ʼ��traveller��
			for (int j=0;j<travellers.size();j++)
			{
				Traveller traveller=travellers.get(j);
				PNode node=traveller.node;
				if (node.childMap.containsKey(c))
				{
					traveller.node=node.childMap.get(c);
					int pI=traveller.node.patternIndex;
					if (pI!=-1)//�ҵ��滻��
					{
						if(this.times[pI]!=0)//�������滻
						{
							this.times[pI]--;
							str.replace(traveller.startPoint, i+1, this.dest[pI]);	//replace;
							//clear all travellers;
							travellers.clear();
							//�޸�i��
							i=i+this.dest[pI].length()-(i+1-traveller.startPoint);
						}
						else	//==0
						{
							invailPattern++;
						}
							
					}
				}
				else
				{
					travellers.remove(j);
					j--;
				}
			}
			//���������traveller��״̬��
			if (invailPattern==this.source.length)
				break;
		}
		return str.toString();
	}
	
	public String toTreeString()
	{
		return this.preTree.toTabString();
	}
	
	public static void main(String argv[])
	{
		//for test
		String str[]={"1234","1321","1243","2132","1235"};
		String replace[]={"a","b","c","d","e"};
		int[] times={-1,-1,-1,1,-1};
		StringReplacer sr=new StringReplacer(str,replace,times);
	//	System.out.println(sr.preTree.toTabString());	
		String string="121321121321";
		System.out.println(sr.replace(string));
	}
	
	class Traveller
	{
		PNode node;
		int startPoint;
		public Traveller(PNode node,int startPoint)
		{
			this.node=node;
			this.startPoint=startPoint;
		}
	}
	
	class PreFixTree
	{
		private PNode root=null;
		public PreFixTree()
		{
			this.root=new PNode();
			this.root.c="ROOT";
			this.root.patternIndex=-2;//-2 means root
		}
		public String toTabString()
		{
			StringBuffer sb=new StringBuffer("");
			sb.append(this.root.c+"\n");
			this.addChildString(this.root, sb, "  ");
			return sb.toString();
		}
		public void addChildString(PNode father,StringBuffer output,String tab)
		{
			for (String str:father.childMap.keySet())
			{
				PNode node=father.childMap.get(str);
				output.append(tab+node.c+" "+node.patternIndex+"\n");
				this.addChildString(node, output, tab+"  ");
			}	
		}
		
	}
	class PNode
	{
		private String c;
		private int patternIndex=-1;
		private HashMap<String,PNode> childMap=new HashMap<String,PNode>();
		public PNode addChild(String c,int patternIndex)
		{
			if (this.childMap.containsKey(c))
				return this.childMap.get(c);
			else
			{
				PNode node=new PNode();
				node.c=c;
				node.patternIndex=patternIndex;
				this.childMap.put(c, node);
				return node;
			}
		}
	}
}
