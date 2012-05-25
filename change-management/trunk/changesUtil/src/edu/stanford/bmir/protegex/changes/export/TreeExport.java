package edu.stanford.bmir.protegex.changes.export;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract class that traverse a generic tree and outputs it in a CSV file.
 *
 * @author Csongor Nyulas
 *
 * @param <N> - the tree node
 */
public abstract class TreeExport<N> {

    private static Logger log = Logger.getLogger(TreeExport.class.getName());

    private static final String SEPARATOR = "\t";
    private static final String QUOTE_CHAR = "\"";


    abstract public String getTreeNodeName(N treeNode);
    abstract public List<N> getTreeNodeChildren(N treeNode);

    public void printTree(N root, Writer w) throws IOException {
        printHeader(w);
        if (root != null) {
            List<N> children = getTreeNodeChildren(root);
            int childCount = (children == null ? 0 :children.size());

            Set<N> visited = new HashSet<N>();
            printTreeNode(root, 0, root, childCount, false, w);
            visited.add(root);

            if (childCount > 0) {
                for (N childNode : children) {
                    recursivePrintTree(childNode, 1, childNode, visited, w);
                }
            }
        }
    }

    private void recursivePrintTree(N node, int level, N topParent, Set<N> visited, Writer w) throws IOException {
        if (node != null ) {
            List<N> children = getTreeNodeChildren(node);
            int childCount = (children == null ? 0 :children.size());

            if (visited.contains(node)) {
                printTreeNode(node, level, topParent, childCount, true, w);
            }
            else {
                printTreeNode(node, level, topParent, childCount, false, w);
                visited.add(node);

                if (childCount > 0) {
                    for (N childNode : children) {
                        recursivePrintTree(childNode, level + 1, topParent, visited, w);
                    }
                }
            }
        }
    }

    private void printHeader(Writer w) throws IOException {
        w.write("name" + SEPARATOR + "level" + SEPARATOR +
                "top_parent" + SEPARATOR + "child_count" + SEPARATOR + "is_duplicate" + "\n");
    }

    private void printTreeNode(N node, int level, N topParent,
            int childCount, boolean isDuplicate, Writer w) throws IOException {
        w.write(formatTreeNode(node) + SEPARATOR + level + SEPARATOR +
                formatTreeNode(topParent) + SEPARATOR + childCount + SEPARATOR + isDuplicate + "\n");
    }

    private String formatTreeNode(N node){
        if (node == null) {
            return "";
        }
        else {
            String s = getTreeNodeName(node);
            if (s.contains(SEPARATOR)) {
                return quote(s);
            }
            else {
                return s;
            }
        }
    }

    private String quote(String s) {
        return QUOTE_CHAR + s.replaceAll("\\" + QUOTE_CHAR, QUOTE_CHAR + QUOTE_CHAR) + QUOTE_CHAR;
    }


       /* ----- test code ----- */

    public static void mainTest(String[] args) {
        //String fileName = args[0];
        String fileName = outputFile;

        TreeExport<TestTreeNode> t = new TreeExport<TestTreeNode>(){
            @Override
            public String getTreeNodeName(TestTreeNode node) {
                return node.getNodeName();
            }
            @Override
            public List<TestTreeNode> getTreeNodeChildren(TestTreeNode node) {
                return node.getChildren();
            }
        };

        try {
            long startTime = System.currentTimeMillis();
            Writer w = new FileWriter(new File(fileName));
            t.printTree(sampleTree, w);
            w.close();
            log.info("Done! Finished in " + ((System.currentTimeMillis() - startTime) / 1000.0) + " seconds. " +
                    "Please see result in " + outputFile);
        } catch (IOException e) {
            log.log(Level.WARNING, e.getMessage(), e);
        }
    }


    private static class TestTreeNode {
        private String name;
        private List<TestTreeNode> children;

        public TestTreeNode(String name) {
            this.name = name;
        }
        public TestTreeNode(String name, List<TestTreeNode> children) {
            this.name = name;
            setChildren(children);
        }
        public void setChildren(List<TestTreeNode> children) {
            this.children = children;
        }

        String getNodeName() {return name;}
        List<TestTreeNode> getChildren() {return children;}
    }

    private static String outputFile = System.getenv("TEMP") + File.separator + "test_print_tree.txt";


    private static TestTreeNode sampleTree =
            new TestTreeNode("root", Arrays.asList( new TestTreeNode[] {
                    new TestTreeNode("Level1_ch1", Arrays.asList( new TestTreeNode[] {
                            new TestTreeNode("Level2_ch11"),
                            new TestTreeNode("\tLevel2_\"ch12\"")})),
                    new TestTreeNode("Level1_ch2")}));

    /* ----- end test data ---- */

}