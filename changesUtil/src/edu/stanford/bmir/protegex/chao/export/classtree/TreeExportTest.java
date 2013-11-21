package edu.stanford.bmir.protegex.chao.export.classtree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Test for the TreeExport class.
 *
 * @author Csongor Nyulas
 */
public class TreeExportTest {

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

    
    public static String tempDir = (System.getenv("TEMP") != null ? System.getenv("TEMP") :
		((System.getenv("TMP") != null ? System.getenv("TMP") : System.getenv("TMPDIR"))));
    public static String outputFile = tempDir + File.separator + "test_print_tree.txt";
    public static TestTreeNode sampleTree =
            new TestTreeNode("root", Arrays.asList( new TestTreeNode[] {
                    new TestTreeNode("Level1_ch1", Arrays.asList( new TestTreeNode[] {
                            new TestTreeNode("Level2_'ch1,1'"),
                            new TestTreeNode("\tLevel2_\"ch1,2\"", Arrays.asList( new TestTreeNode[] {
                                    new TestTreeNode("Level3_ch1,2,1"),
                                    new TestTreeNode("Level2_ch1,2,2")}))})),
                    new TestTreeNode("Level1_ch2 (aka A2)", Arrays.asList( new TestTreeNode[] {
                            new TestTreeNode("B2.1"),
                            new TestTreeNode("B2.2", Arrays.asList( new TestTreeNode[] {
                                    new TestTreeNode("C2.2.1"),
                                    new TestTreeNode("C2.2.2")}))}))}));

    
    public static Logger log = Logger.getLogger(TreeExportTest.class.getName());

    
    public static void main(String[] args) {
        //String fileName = args[0];
        String fileName = outputFile;

        TreeExport<TestTreeNode> t = new TreeExport<TestTreeNode>(){
            @Override
            public String getTreeNodeName(TestTreeNode node) {
                return node.getNodeName();
            }
            @Override
            public String getTreeNodeDisplayText(TestTreeNode node) {
            	return "";
            }
            @Override
            public List<TestTreeNode> getTreeNodeChildren(TestTreeNode node) {
                return node.getChildren();
            }
        };
        t.setCsvSeparator(",");

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

}