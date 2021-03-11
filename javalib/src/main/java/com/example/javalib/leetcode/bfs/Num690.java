package com.example.javalib.leetcode.bfs;

import org.omg.CORBA.PUBLIC_MEMBER;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * BFS
 * <p>
 * 给定一个保存员工信息的数据结构，它包含了员工唯一的id，重要度 和 直系下属的id。
 * <p>
 * 比如，员工1是员工2的领导，员工2是员工3的领导。他们相应的重要度为15, 10, 5。那么员工1的数据结构是[1, 15, [2]]，员工2的数据结构是[2, 10, [3]]，员工3的数据结构是[3, 5, []]。注意虽然员工3也是员工1的一个下属，但是由于并不是直系下属，因此没有体现在员工1的数据结构中。
 * <p>
 * 现在输入一个公司的所有员工信息，以及单个员工id，返回这个员工和他所有下属的重要度之和。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/employee-importance
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 * <p>
 * <p>
 * PS:很吊、自己就是套一个BFS的框架 就直接通过了
 */
public class Num690 {
    public static void main(String[] args) {
    }

    public int getImportance(List<Employee> employees, int id) {
        Employee employee = get(employees, id);
        if (employee != null) {
            return bfs(employees, employee);
        }
        return 0;
    }

    public Employee get(List<Employee> employees, int id) {
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).id == id) {
                return employees.get(i);
            }
        }
        return null;
    }

    public int bfs(List<Employee> employees, Employee employee) {
        Queue<Employee> queue = new LinkedList<>();
        queue.offer(employee);
        int weight = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Employee employee1 = queue.poll();
                if (employee1 == null) {
                    continue;
                }
                weight += employee1.importance;
                if (employee1.subordinates != null && !employee1.subordinates.isEmpty()) {
                    for (int j = 0; j < employee1.subordinates.size(); j++) {
                        queue.offer(get(employees, employee1.subordinates.get(j)));
                    }
                }
            }
        }
        return weight;
    }

    public static class Employee {
        public int id;
        public int importance;
        public List<Integer> subordinates;
    }

    ;
}
