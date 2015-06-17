package org.eclipse.sapphire.ui.swt.gef.internal;

import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.swt.widgets.Control;

/**
 * copy from {@link org.eclipse.jface.fieldassist.AutoCompleteField}
 *
 * @author tds
 * @createtime 2014年11月17日 下午4:35:47
 */
public class AutoCompleteField {
	private SimpleContentProposalProvider proposalProvider;
	private ContentProposalAdapter adapter;

	/**
	 * Construct an AutoComplete field on the specified control, whose
	 * completions are characterized by the specified array of Strings.
	 * 
	 * @param control
	 *            the control for which autocomplete is desired. May not be
	 *            <code>null</code>.
	 * @param controlContentAdapter
	 *            the <code>IControlContentAdapter</code> used to obtain and
	 *            update the control's contents. May not be <code>null</code>.
	 * @param proposals
	 *            the array of Strings representing valid content proposals for
	 *            the field.
	 */
	public AutoCompleteField(final Control control,
			IControlContentAdapter controlContentAdapter, String[] proposals) {
		proposalProvider = new SimpleContentProposalProvider(proposals);
		proposalProvider.setFiltering(true);
		adapter = new ContentProposalAdapter(control, controlContentAdapter,
				proposalProvider, null, null);
		adapter.setPropagateKeys(true);
		adapter
				.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
	}

	/**
	 * Set the Strings to be used as content proposals.
	 * 
	 * @param proposals
	 *            the array of Strings to be used as proposals.
	 */
	public void setProposals(String[] proposals) {
		proposalProvider.setProposals(proposals);
	}
}
