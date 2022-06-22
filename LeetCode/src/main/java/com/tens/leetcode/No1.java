package com.tens.leetcode;

import java.util.HashMap;

public class No1 {
    public int[] twoSum(int[] nums, int target) {
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int num = target - nums[i];
            if (map.containsKey(num)) {
                return new int[]{map.get(num), i + 1};
            }
            map.put(nums[i], i + 1);
        }
        return null;
    }

    public static void main(String[] args) {
        No1 solution = new No1();
        int[] ints = solution.twoSum(new int[]{1, 1, 5, 9, 10, 22}, 2);
        System.out.println(ints[0] + ":" + ints[1]);
    }
}
