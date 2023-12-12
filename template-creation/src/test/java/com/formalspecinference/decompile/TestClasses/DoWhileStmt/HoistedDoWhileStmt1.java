public static boolean func() {
	int count = 0;
	boolean var_a = false;

	do {
		if (!var_a) var_a = true;
		else {
			int var_b = isLessThanTen(count);
			if (!var_b) break;
		}

		System.out.println("Count: " + count);
		count++;
	} while (true);
	return false;
}
