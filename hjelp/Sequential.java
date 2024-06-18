/**
 * Sequential
 */
public class Sequential {

    
    public static IntList get_enfolding(int[] x, int[] y){
        IntList index_list = new IntList((int) Math.sqrt(x.length));
        int index_high_x = -1;
        int index_low_x = -1;
        int high_x = x[0];
        int low_x = x[0];
        for (int i = 1; i < x.length; i++) {
            if (x[i] > high_x) {
                index_high_x = i;
                high_x = x[i];
            }
            if (x[i] < low_x) {
                index_low_x = i;
                low_x = x[i];
            }
        }
        // System.out.printf("Index high-x: %d val: %d \t Index low-x: %d val: %d\n", index_high_x, high_x, index_low_x, low_x);
        int min_dist_right_index = 0;
        int max_dist_left_index = 0;
        double min_dist_right = dist_to_line(index_high_x, index_low_x, min_dist_right_index, x, y);
        double max_dist_left = min_dist_right;
        IntList above_line = new IntList(x.length / 2);
        IntList under_line = new IntList(x.length / 2);
        for (int i = 1; i < x.length; i++) {
            double new_dist_to_line = dist_to_line(index_high_x, index_low_x, i, x, y);
            if (new_dist_to_line < 0) {
                above_line.add(i);
            }
            if (new_dist_to_line > 0) {
                under_line.add(i);
            }
            if (new_dist_to_line < min_dist_right){
                min_dist_right = new_dist_to_line;
                min_dist_right_index = i;
            }
            if (new_dist_to_line > max_dist_left) {
                max_dist_left = new_dist_to_line;
                max_dist_left_index = i;
            }
        }
        // System.out.printf("Index upper point: %d dist: %f \t Index lower point: %d dist: %f\n", min_dist_right_index, min_dist_right, max_dist_left_index, max_dist_left);
        
        index_list.add(index_high_x);
        // seek top_right
        IntList top_right = sek_Rek_Right(index_high_x, min_dist_right_index, above_line, x, y);
        index_list.append(top_right);
        index_list.add(min_dist_right_index);

        //seek top left
        IntList top_left = sek_Rek_Right(min_dist_right_index, index_low_x, above_line, x, y);
        index_list.append(top_left);
        index_list.add(index_low_x);
        
        //seek bottom left
        IntList bottom_left = sek_Rek_Right(index_low_x, max_dist_left_index, under_line, x, y);
        index_list.append(bottom_left);
        index_list.add(max_dist_left_index);

        //seek bottom right
        IntList bottom_right = sek_Rek_Right(max_dist_left_index, index_high_x, under_line, x, y);
        index_list.append(bottom_right);
        index_list.add(index_high_x);

        return index_list;
    }

    public static IntList sek_Rek_Right(int index_line_start, int index_line_end, IntList where_to_check, int[] x, int[] y){
        IntList index_list = new IntList(10);
        int min_dist_right_index = 0;
        double min_dist_right = 0;
        boolean found_outer = false;
        IntList to_the_right = new IntList(where_to_check.len / 2);
        for (int i = 0; i < where_to_check.len; i++) {
            int index_to_check = where_to_check.get(i);
            if (index_to_check == index_line_start || index_to_check == index_line_end) {
                continue;
            }
            double new_dist_to_line = dist_to_line(index_line_start, index_line_end, index_to_check, x, y);
            if (new_dist_to_line < 0) {
                to_the_right.add(index_to_check);
            }
            if (new_dist_to_line < min_dist_right){
                min_dist_right = new_dist_to_line;
                min_dist_right_index = index_to_check;
                found_outer = true;
            }
            
        }
        if (found_outer) {
            IntList children_list_1 = sek_Rek_Right(index_line_start, min_dist_right_index,to_the_right, x, y);
            IntList children_list_2 = sek_Rek_Right(min_dist_right_index, index_line_end,to_the_right, x, y);
            index_list.append(children_list_1);
            index_list.add(min_dist_right_index);
            index_list.append(children_list_2);
            return index_list;
        }
        return index_list;
    }


    public static double dist_to_line(int index_line_start, int index_line_end, int index_new_point, int[] x, int[] y){
        int x1 = x[index_line_start];
        int y1 = y[index_line_start];
        
        int x2 = x[index_line_end];
        int y2 = y[index_line_end];
        
        int x_new = x[index_new_point];
        int y_new = y[index_new_point];
        
        int a = y1 - y2;
        int b = x2 - x1;
        int c = y2*x1 - y1*x2;
        
        double dist = (a * x_new + b * y_new + c ) / Math.sqrt(a * a + b * b);
        return dist;
    }

    public static int findMax(int[] a) {
        int max = a[0];
        for (int i = 1; i < a.length; i++) {
            if (a[i] > max) {
                max = a[i];
            }
        }
        return max;
    }


}