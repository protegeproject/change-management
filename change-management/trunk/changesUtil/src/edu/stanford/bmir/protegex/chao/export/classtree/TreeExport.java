package edu.stanford.bmir.protegex.chao.export.classtree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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

    public static final String SEPARATOR = "\t";
    public static final String QUOTE_CHAR = "\"";
	public static final CharSequence NEW_LINE = "\n";

    private static Logger log = Logger.getLogger(TreeExport.class.getName());
    
    private String separator = SEPARATOR;
    private String quote = QUOTE_CHAR;

    /**
     * The implementaion of this method should return a name or identifier for the tree node.
     * 
     * @param treeNode an instance representing a tree node
     * @return the id of the tree node
     */
    abstract public String getTreeNodeName(N treeNode);
    /**
     * The implementaion of this method should return a user friendly display text for the tree node.
     * 
     * @param treeNode an instance representing a tree node
     * @return the user friendly display text of the tree node
     */
    abstract public String getTreeNodeDisplayText(N treeNode);
    /**
     * The implementaion of this method should return the children of the tree node.
     * 
     * @param treeNode an instance representing a tree node
     * @return the list of children tree nodes of the given tree node
     */
    abstract public List<N> getTreeNodeChildren(N treeNode);

    
    public String getCsvSeparator() {
    	return separator;
    }
    public void setCsvSeparator(String newSeparator) {
    	separator = newSeparator;
    }
    
    public String getCsvQuoteChar() {
    	return quote;
    }
    public void setCsvQuoteChar(String newQuoteChar) {
    	quote = newQuoteChar;
    }
    
    
    public void printTree(N root, File file) {
    	Writer w = null;
		try {
			w = new FileWriter(file);
			printTree(root, w);
		} catch (IOException e) {
			log.log(Level.WARNING, e.getMessage(), e);
		} finally {
			try {
				if (w != null) {
					w.close();
				}
			} catch (Exception e) {
	            log.log(Level.WARNING, e.getMessage(), e);
			}
		}
    }
    
    public void printTree(N root, Writer w) throws IOException {
        printHeader(w);
        if (root != null) {
            List<N> children = getTreeNodeChildren(root);
            int childCount = (children == null ? 0 :children.size());

            Set<N> visited = new HashSet<N>();
            printTreeNode(root, 0, root, root, childCount, false, w);
            visited.add(root);

            if (childCount > 0) {
                for (N childNode : children) {
                    recursivePrintTree(childNode, 1, childNode, root, visited, w);
                }
            }
        }
    }

    private void recursivePrintTree(N node, int level, N topParent, N parent, Set<N> visited, Writer w) throws IOException {
        if (node != null ) {
            List<N> children = getTreeNodeChildren(node);
            int childCount = (children == null ? 0 :children.size());

            if (visited.contains(node)) {
                printTreeNode(node, level, topParent, parent, childCount, true, w);
            }
            else {
                printTreeNode(node, level, topParent, parent, childCount, false, w);
                visited.add(node);

                if (childCount > 0) {
                    for (N childNode : children) {
                        recursivePrintTree(childNode, level + 1, topParent, node, visited, w);
                    }
                }
            }
        }
    }

    private void printHeader(Writer w) throws IOException {
        w.write("name" + separator + "display_text" + separator + "level" + separator + 
        		"parent" + separator + "top_parent" + separator + "child_count" + separator + 
        		"is_duplicate" + NEW_LINE);
    }

    private void printTreeNode(N node, int level, N topParent, N parent,
            int childCount, boolean isDuplicate, Writer w) throws IOException {
    	String nodeName = (node == null ? null : getTreeNodeName(node));
    	String nodeDisplayText = (node == null ? null : getTreeNodeDisplayText(node));
    	String parentName = (node == null ? null : getTreeNodeName(parent));
    	String topParentName = (node == null ? null : getTreeNodeName(topParent));
    	
        w.write(formatTreeNodeInfo(nodeName) + separator + formatTreeNodeInfo(nodeDisplayText) + separator + 
        		level + separator + formatTreeNodeInfo(parentName) + separator + 
                formatTreeNodeInfo(topParentName) + separator + childCount + separator + 
                isDuplicate + NEW_LINE);
    }

    private String formatTreeNodeInfo(String s){
        if (s == null) {
            return "";
        }
        else {
            if (s.contains(separator) || s.contains(NEW_LINE)) {
                return quote(s);
            }
            else {
                return s;
            }
        }
    }

    private String quote(String s) {
        return quote + s.replaceAll("\\" + quote, quote + quote) + quote;
    }


}