class Result {

	public static List<Integer> kthPerson(int k, List<Integer> patience, List<Integer> query) 
	{
		
		int capacity = k;
		//int busFilled = 0;
		int qSize = query.size() - 1;
		int pSize = patience.size() - 1;
		List<Integer> kth = new ArrayList<Integer>();
		int busFilled = 0;
	
		for(int q=0;q<=qSize;q++ )
		{
			int curr = query.get(q);
			busFilled=0;
			patience:for(int p=0;p<=pSize;p++ )
			{
				
				if(patience.get(p) >= query.get(q) )
				{
					
					busFilled ++;
					if(p == pSize && busFilled < capacity)
					{
						kth.add(0);
					}
					else if(busFilled == capacity)
					{
						kth.add(p+1);
						break patience;
					}
				}
				else if(p == pSize && busFilled < capacity)
				{
					kth.add(0);
				}
			}
		}
		return kth;
	}
}